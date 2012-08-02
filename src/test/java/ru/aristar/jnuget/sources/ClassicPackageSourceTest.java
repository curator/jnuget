package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.Description;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.ClassicNupkg;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.sources.push.PushStrategy;
import ru.aristar.jnuget.sources.push.BeforeTrigger;
import ru.aristar.jnuget.sources.push.NugetPushException;

/**
 * Тесты классического (все пакеты в одной папке) хранилища NuGet
 *
 * @author sviridov
 */
public class ClassicPackageSourceTest {

    /**
     * Тестовая папка с пакетами
     */
    private static File testFolder;
    /**
     * Контекст создания заглушек
     */
    private Mockery context = new Mockery();
    /**
     * Идентификатор заглушки
     */
    private int mockId = 0;

    /**
     * Создает идентификатор фала пакета
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return идентификатор фала пакета
     * @throws NugetFormatException некорректный формат версии
     */
    private Nupkg createNupkg(final String id, final String version) throws NugetFormatException {
        final Nupkg pack = context.mock(Nupkg.class, "nupkg" + (mockId++));
        context.checking(new Expectations() {
            {
                atLeast(0).of(pack).getId();
                will(returnValue(id));
                atLeast(0).of(pack).getVersion();
                will(returnValue(Version.parse(version)));
            }
        });

        return pack;
    }

    /**
     * Создание тестового каталога и наполнение его файлами
     *
     * @throws IOException Ошибка копирования файла пкета
     */
    @BeforeClass
    public static void createTestFolder() throws IOException {
        File file = File.createTempFile("tmp", "tst");
        testFolder = new File(file.getParentFile(), "TestFolder/");
        testFolder.mkdir();
        String[] resources = new String[]{"/NUnit.2.5.9.10348.nupkg"};
        for (String resource : resources) {
            File targetFile = new File(testFolder, resource.substring(1));
            try (ReadableByteChannel sourceChannel = Channels.newChannel(ClassicPackageSourceTest.class.getResourceAsStream(resource));
                    FileChannel targetChannel = new FileOutputStream(targetFile).getChannel();) {
                TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
            }
        }
    }

    /**
     * Удаление тестового каталога
     *
     * @throws IOException Ошибка удаления тестового каталога
     */
    @AfterClass
    public static void removeTestFolder() throws IOException {
        if (testFolder != null && testFolder.exists()) {
            FileUtils.deleteDirectory(testFolder);
        }
    }

    /**
     * Проверка чтения пакетов из каталога
     *
     * @throws NugetFormatException некорректный формат спецификации файла
     */
    @Test
    public void testReadFilesFromFolder() throws NugetFormatException {
        //GIVEN
        ClassicPackageSource packageSource = new ClassicPackageSource(testFolder);
        //WHEN
        Collection<ClassicNupkg> packages = packageSource.getPackages();
        //THEN
        assertEquals("Прочитано файлов", 1, packages.size());
        assertEquals("Идентификатор пакета", "NUnit", packages.iterator().next().getNuspecFile().getId());
    }

    /**
     * Если публикация запрещена - пакет не публикуется
     *
     * @throws IOException ошибка чтения тестового пакета
     * @throws NugetFormatException ошибка в формате тестового пакета
     */
    @Test
    public void testWhenNotAllow() throws IOException, NugetFormatException {
        //GIVEN 
        File tmpFolder = File.createTempFile("NotAllow", "NotAllow");
        File tmpTestFolder = new File(tmpFolder, "NotAllow");
        tmpTestFolder.mkdirs();
        try {
            ClassicPackageSource classicPackageSource = new ClassicPackageSource(tmpTestFolder);
            PushStrategy simplePushStrategy = new PushStrategy(false);
            classicPackageSource.setPushStrategy(simplePushStrategy);
            TempNupkgFile nupkgFile = new TempNupkgFile(this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg"));
            //WHEN
            boolean success = classicPackageSource.pushPackage(nupkgFile);
            //THEN
            assertThat("Пакет опубликован", success, is(equalTo(false)));
            assertThat("В тестовом каталоге не создано пакетов", tmpTestFolder.list(), is(nullValue()));
        } finally {
            FileUtils.deleteDirectory(tmpTestFolder);
        }
    }

    /**
     * Проверка метода, извлекающего из списка идентификаторов последние версии
     * пакетов
     *
     * @throws NugetFormatException некорректный формат версии в тестовых данных
     */
    @Test
    public void testGetLastVersions() throws NugetFormatException {
        //GIVEN
        Collection<Nupkg> idList = new ArrayList<>();
        idList.add(createNupkg("A", "1.1.1"));
        idList.add(createNupkg("A", "1.1.2"));
        idList.add(createNupkg("A", "1.2.1"));
        Nupkg lastA = createNupkg("A", "2.1.1");
        idList.add(lastA);
        idList.add(createNupkg("B", "2.1.1"));
        Nupkg lastB = createNupkg("B", "5.1.1");
        idList.add(lastB);
        //WHEN
        Collection<Nupkg> result = ClassicPackageSource.extractLastVersion(idList);
        Nupkg[] resArr = result.toArray(new Nupkg[0]);
        Arrays.sort(resArr, new Comparator<Nupkg>() {
            @Override
            public int compare(Nupkg o1, Nupkg o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        //THEN 
        assertArrayEquals("Должны возвращаться только последние версии", new Nupkg[]{lastA, lastB}, resArr);
    }

    /**
     * Проверка срабатывания триггера при помещении пакета в хранилище
     *
     * @throws IOException ошибка доступа к тестовому пакету
     * @throws NugetPushException ошибка публикации пакета в хранилище
     */
    @Test
    public void testProcessTrigger() throws IOException, NugetPushException {
        //GIVEN
        final ClassicPackageSource classicPackageSource = new ClassicPackageSource(testFolder);
        PushStrategy simplePushStrategy = new PushStrategy(true);
        classicPackageSource.setPushStrategy(simplePushStrategy);
        List<Nupkg> pushedPackages = new ArrayList<>();
        //Пакет
        final Nupkg nupkg = context.mock(Nupkg.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(nupkg).getFileName();
        expectations.will(returnValue("NUnit.2.5.9.10348.nupkg"));
        expectations.atLeast(0).of(nupkg).getStream();
        expectations.will(returnValue(this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg")));
        //Триггер
        final BeforeTrigger trigger = context.mock(BeforeTrigger.class);
        expectations.oneOf(trigger).doAction(nupkg, classicPackageSource);
        expectations.will(new CallBackAction(pushedPackages));

        context.checking(expectations);
        simplePushStrategy.getBeforeTriggers().add(trigger);

        //WHEN
        classicPackageSource.pushPackage(nupkg);
        assertArrayEquals("Пакеты для которых вызывался триггер", new Nupkg[]{nupkg}, pushedPackages.toArray(new Nupkg[0]));

    }

    /**
     * Действие, выполняемое при срабатывание триггера
     */
    protected class CallBackAction implements Action {

        /**
         * Список для добавления удаленных версий
         */
        public final List<Nupkg> packages;

        /**
         * @param packages список, в который помещаются пакеты, для которых
         * вызывался метод
         */
        public CallBackAction(List<Nupkg> packages) {
            this.packages = packages;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Собирает список вызовов метода в коллекцию");
        }

        @Override
        public Object invoke(Invocation invocation) throws Throwable {
            Object firstArgument = invocation.getParameter(0);
            Nupkg nupkg = (Nupkg) firstArgument;
            packages.add(nupkg);
            return true;
        }
    }
}

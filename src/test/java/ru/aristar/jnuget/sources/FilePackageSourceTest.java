package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class FilePackageSourceTest {

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
     * @throws Exception некорректный формат версии
     */
    private Nupkg createNupkg(final String id, final String version) throws Exception {
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
     * @throws IOException
     */
    @BeforeClass
    public static void createTestFolder() throws IOException {
       File file = File.createTempFile("tmp", "tst");
        testFolder = new File(file.getParentFile(), "TestFolder/");
        testFolder.mkdir();
        String[] resources = new String[]{"/NUnit.2.5.9.10348.nupkg"};
        for (String resource : resources) {
            File targetFile = new File(testFolder, resource.substring(1));
            try (ReadableByteChannel sourceChannel = Channels.newChannel(FilePackageSourceTest.class.getResourceAsStream(resource));
                    FileChannel targetChannel = new FileOutputStream(targetFile).getChannel();) {
                TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
            }
        }
    }

    /**
     * Удаление тестового каталога
     */
    @AfterClass
    public static void removeTestFolder() {
        if (testFolder != null && testFolder.exists()) {
            testFolder.delete();
        }

    }

    /**
     * Проверка чтения пакетов из каталога
     */
    @Test
    public void testReadFilesFromFolder() {
        //GIVEN
        FilePackageSource packageSource = new FilePackageSource(testFolder);
        //WHEN
        Collection<Nupkg> packages = packageSource.getPackages();
        //THEN
        assertEquals("Прочитано файлов", 1, packages.size());
        assertEquals("Идентификатор пакета", "NUnit", packages.iterator().next().getNuspecFile().getId());
    }

    /**
     * Проверка метода, извлекающего из списка идентификаторов последние версии пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetLastVersions() throws Exception {
        //GIVEN
        FilePackageSource filePackageSource = new FilePackageSource();
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
        Collection<Nupkg> result = filePackageSource.extractLastVersion(idList, true);
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
}

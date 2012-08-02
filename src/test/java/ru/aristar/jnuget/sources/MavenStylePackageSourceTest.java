package ru.aristar.jnuget.sources;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.equalTo;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.MavenNupkg;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.files.nuspec.NuspecFile;
import ru.aristar.jnuget.sources.push.PushStrategy;

/**
 * Тест источника данных, хранящего пакеты в структуре каталогов схожей со
 * структурой Maven
 *
 * @author sviridov
 */
public class MavenStylePackageSourceTest {

    /**
     * Контекст для создания заглушек
     */
    private Mockery context = new Mockery();
    /**
     * Тестовая папка с пакетами
     */
    private static File testFolder;

    /**
     * Создание тестового каталога и наполнение его файлами
     *
     * @throws IOException
     */
    @BeforeClass
    public static void createTestFolder() throws IOException {
        File file = File.createTempFile("tmp", "tst");
        Pattern pattern = Pattern.compile("/(.+)\\.(\\d+\\.\\d+\\.\\d+\\.\\d+)\\.nupkg", Pattern.CASE_INSENSITIVE);
        testFolder = new File(file.getParentFile(), "TestFolder/");
        removeTestFolder();
        testFolder.mkdir();
        String resource = "/NUnit.2.5.9.10348.nupkg";

        Matcher matcher = pattern.matcher(resource);
        matcher.matches();
        String packageId = matcher.group(1).toLowerCase();
        File packageFolder = new File(testFolder, packageId + "/");
        packageFolder.mkdirs();
        File versionFolder = new File(packageFolder, matcher.group(2) + "/");
        versionFolder.mkdirs();
        File targetFile = new File(versionFolder, resource.substring(1));
        try (ReadableByteChannel sourceChannel = Channels.newChannel(MavenStylePackageSourceTest.class.getResourceAsStream(resource));
                FileChannel targetChannel = new FileOutputStream(targetFile).getChannel()) {
            TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
        }
        File hashfile = new File(versionFolder, MavenNupkg.HASH_FILE_NAME);
        try (FileWriter fileWriter = new FileWriter(hashfile)) {
            fileWriter.write("kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==");
        }
        File nuspecFile = new File(versionFolder, MavenNupkg.NUSPEC_FILE_NAME);
        try (ReadableByteChannel sourceChannel = Channels.newChannel(MavenStylePackageSourceTest.class.getResourceAsStream("/nuspec/NUnit.nuspec.xml"));
                FileChannel targetChannel = new FileOutputStream(nuspecFile).getChannel()) {
            TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
        }
    }

    /**
     * Удаление тестового каталога
     *
     * @throws IOException ошибка удаления временного каталога
     */
    @AfterClass
    public static void removeTestFolder() throws IOException {
        if (testFolder != null && testFolder.exists()) {
            FileUtils.deleteDirectory(testFolder);
        }

    }

    /**
     * Получение списка пакетов с указанным идентификатором
     *
     * @throws Exception ошибка в процессетеста
     */
    @Test
    public void testGetPackagesById() throws Exception {
        //GIVEN
        MavenStylePackageSource packageSource = new MavenStylePackageSource(testFolder);
        //WHEN
        Collection<MavenNupkg> result = packageSource.getPackages("NUnit");
        //THEN
        assertNotNull("Коллекция пакетов", result);
        assertEquals("Пакетов в коллекции", 1, result.size());
        Nupkg resultNupkg = result.iterator().next();
        try (InputStream nupkgStream = resultNupkg.getStream()) {
            assertEquals("Идентификатор пакета", "NUnit", resultNupkg.getId());
            assertEquals("Хеш объекта", "kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==", resultNupkg.getHash().toString());
            assertNotNull("Поток с данными пакета получен", nupkgStream);
        }
    }

    /**
     * Получение списка пакетов с указанным идентификатором, если нет каталога
     * для данного идентификатора пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetPackagesByIdIfNotExist() throws Exception {
        //GIVEN
        MavenStylePackageSource packageSource = new MavenStylePackageSource(testFolder);
        //WHEN
        Collection<MavenNupkg> result = packageSource.getPackages("NO_PACKAGE_IN_STORAGE");
        //THEN
        assertNotNull("Коллекция пакетов", result);
        assertTrue("Коллекция пуста", result.isEmpty());
    }

    /**
     * Получение списка пакетов с указанным идентификатором, если каталог с
     * идентификаторо мпакета пуст
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetPackagesByIdIfEmptyIdFolder() throws Exception {
        //GIVEN
        MavenStylePackageSource packageSource = new MavenStylePackageSource(testFolder);
        File noversionIdFolder = new File(testFolder, "NO_VERSION_IN_STORAGE");
        noversionIdFolder.mkdirs();
        //WHEN
        Collection<MavenNupkg> result = packageSource.getPackages("NO_VERSION_IN_STORAGE");
        //THEN
        assertNotNull("Коллекция пакетов", result);
        assertTrue("Коллекция пуста", result.isEmpty());
    }

    /**
     * проверка получения спецификации пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetPackageSpecification() throws Exception {
        //GIVEN
        MavenStylePackageSource packageSource = new MavenStylePackageSource(testFolder);
        //WHEN
        Collection<MavenNupkg> nupkgs = packageSource.getPackages("NUnit");
        //THEN
        assertThat("Пакет найден", nupkgs.size(), equalTo(1));
        NuspecFile result = nupkgs.iterator().next().getNuspecFile();
        assertNotNull("Спецификация пакета", result);
        assertEquals("Идентификатор пакета", "NUnit", result.getId());
        assertEquals("Версия пакета", Version.parse("2.5.9.10348"), result.getVersion());
    }

    /**
     * Проверка помещения и извлечения пакета из хранилища. Пакет не должен
     * изменятся
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testPushAndGet() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile tempNupkgFile = new TempNupkgFile(inputStream);
        File packageFolder = new File(testFolder, tempNupkgFile.getId());
        if (packageFolder.exists()) {
            FileUtils.deleteDirectory(packageFolder);
        }
        MavenStylePackageSource mavenStylePackageSource = new MavenStylePackageSource(testFolder);
        mavenStylePackageSource.setPushStrategy(new PushStrategy(true));
        mavenStylePackageSource.pushPackage(tempNupkgFile);
        //WHEN
        MavenNupkg result = mavenStylePackageSource.getPackage(tempNupkgFile.getId(), tempNupkgFile.getVersion());
        //THEN
        assertEquals("Идентификатор пакета", tempNupkgFile.getId(), result.getId());
        assertEquals("Версия пакета", tempNupkgFile.getVersion(), result.getVersion());
    }

    /**
     * Проверка помещения пакета в хранилище. Должны создаться все файлы.
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testPush() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile tempNupkgFile = new TempNupkgFile(inputStream);
        File packageFolder = new File(testFolder, tempNupkgFile.getId().toLowerCase());
        if (packageFolder.exists()) {
            FileUtils.deleteDirectory(packageFolder);
        }
        MavenStylePackageSource mavenStylePackageSource = new MavenStylePackageSource(testFolder);
        mavenStylePackageSource.setPushStrategy(new PushStrategy(true));
        //WHEN
        mavenStylePackageSource.pushPackage(tempNupkgFile);
        File versionFolder = new File(packageFolder, "2.5.9.10348");
        File hashFile = new File(versionFolder, MavenNupkg.HASH_FILE_NAME);
        File packageFile = new File(versionFolder, "NUnit.2.5.9.10348.nupkg");
        File nuspecFile = new File(versionFolder, MavenNupkg.NUSPEC_FILE_NAME);
        //THEN
        assertTrue("Файл пакета создан", packageFile.exists());
        assertTrue("Файл спецификации создан", nuspecFile.exists());
        assertTrue("Файл хеша создан", hashFile.exists());
        try (FileChannel hashChanel = new FileInputStream(hashFile).getChannel()) {
            assertTrue("Хеш сгенерирован", hashChanel.size() > 0);
        }
    }

    /**
     * Проверка удаления не последнего пакета с данным идентификатором из
     * репозитория
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testRemovePackage() throws Exception {
        final String packageId = "TEST_PACKAGE";
        final String packageVersionString1 = "1.2.3.4";
        final String packageVersionString2 = "1.2.3.5";
        Nupkg nupkg2 = context.mock(Nupkg.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(nupkg2).getId();
        expectations.will(Expectations.returnValue(packageId));
        expectations.atLeast(0).of(nupkg2).getVersion();
        expectations.will(Expectations.returnValue(Version.parse(packageVersionString2)));
        context.checking(expectations);

        //GIVEN
        File idFolder = new File(testFolder, packageId);

        File versionFolder1 = new File(idFolder, packageVersionString1);
        versionFolder1.mkdirs();
        File versionFolder2 = new File(idFolder, packageVersionString2);
        versionFolder2.mkdirs();
        File packageFile1 = new File(versionFolder1, packageId + "." + packageVersionString1 + Nupkg.DEFAULT_EXTENSION);
        File packageFile2 = new File(versionFolder2, packageId + "." + packageVersionString2 + Nupkg.DEFAULT_EXTENSION);

        try (InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
                FileChannel targetChanel = new FileOutputStream(packageFile1).getChannel()) {
            TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), targetChanel);
        }
        try (InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
                FileChannel targetChanel = new FileOutputStream(packageFile2).getChannel()) {
            TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), targetChanel);
        }
        MavenStylePackageSource packageSource = new MavenStylePackageSource(testFolder);
        //WHEN
        packageSource.removePackage(nupkg2);
        //THEN
        assertTrue("Файл пакета не удален", packageFile1.exists());
        assertTrue("Каталог с версией не удален", versionFolder1.exists());
        assertTrue("Каталог идентификатора не удален", idFolder.exists());
        assertFalse("Файл пакета удален", packageFile2.exists());
        assertFalse("Каталог с версией удален", versionFolder2.exists());
    }

    /**
     * Проверка удаления последнего пакета с данным идентификатором из
     * репозитория (должна удалиться папка идентификатора пакета)
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testRemoveLastPackage() throws Exception {
        //GIVEN
        final String packageId = "TEST_PACKAGE";
        final String packageVersionString = "1.2.3.4";
        Nupkg nupkg = context.mock(Nupkg.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(nupkg).getId();
        expectations.will(Expectations.returnValue(packageId));
        expectations.atLeast(0).of(nupkg).getVersion();
        expectations.will(Expectations.returnValue(Version.parse(packageVersionString)));
        context.checking(expectations);
        File idFolder = new File(testFolder, packageId);

        File versionFolder = new File(idFolder, packageVersionString);
        File packageFile = new File(versionFolder, packageId + "." + packageVersionString + Nupkg.DEFAULT_EXTENSION);
        versionFolder.mkdirs();
        try (InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
                FileChannel targetChanel = new FileOutputStream(packageFile).getChannel()) {
            TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), targetChanel);
        }
        MavenStylePackageSource packageSource = new MavenStylePackageSource(testFolder);
        //WHEN
        packageSource.removePackage(nupkg);
        //THEN
        assertFalse("Файл пакета удален", packageFile.exists());
        assertFalse("Каталог с версией удален", versionFolder.exists());
        assertFalse("Каталог идентификатора удален", idFolder.exists());
    }
}

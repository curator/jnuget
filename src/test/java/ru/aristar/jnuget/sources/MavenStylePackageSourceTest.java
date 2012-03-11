package ru.aristar.jnuget.sources;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.MavenNupkg;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.NuspecFile;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 * Тест источника данных, хранящего пакеты в структуре каталогов схожей со
 * структурой Maven
 *
 * @author sviridov
 */
public class MavenStylePackageSourceTest {

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
        File packageFolder = new File(testFolder, matcher.group(1) + "/");
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
        try (ReadableByteChannel sourceChannel = Channels.newChannel(MavenStylePackageSourceTest.class.getResourceAsStream("/NUnit.nuspec.xml"));
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
        assertEquals("Идентификатор пакета", "NUnit", resultNupkg.getId());
        assertEquals("Хеш объекта", "kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==", resultNupkg.getHash().toString());
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
        NuspecFile result = nupkgs.iterator().next().getNuspecFile();
        //THEN    
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
        mavenStylePackageSource.setPushStrategy(new SimplePushStrategy(true));
        mavenStylePackageSource.pushPackage(tempNupkgFile, null);
        //WHEN
        MavenNupkg result = mavenStylePackageSource.getPackage(tempNupkgFile.getId(), tempNupkgFile.getVersion());
        //THEN
        assertEquals("Идентификатор пакета", tempNupkgFile.getId(), result.getId());
        assertEquals("Версия пакета", tempNupkgFile.getVersion(), result.getVersion());
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
        packageSource.removePackage(packageId, Version.parse(packageVersionString2));
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
        final String packageId = "TEST_PACKAGE";
        final String packageVersionString = "1.2.3.4";
        //GIVEN
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
        packageSource.removePackage(packageId, Version.parse(packageVersionString));
        //THEN
        assertFalse("Файл пакета удален", packageFile.exists());
        assertFalse("Каталог с версией удален", versionFolder.exists());
        assertFalse("Каталог идентификатора удален", idFolder.exists());
    }
}

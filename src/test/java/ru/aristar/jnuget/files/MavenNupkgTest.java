package ru.aristar.jnuget.files;

import java.io.*;
import java.nio.channels.Channels;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 * Тест пакета для хранилища в стиле Maven
 *
 * @author sviridov
 */
public class MavenNupkgTest {

    /**
     * Тестовая папка с пакетами
     */
    private File testFolder;

    /**
     * Создание тестового каталога и наполнение его файлами
     *
     * @throws IOException
     */
    @Before
    public void createTestFolder() throws IOException {
        File file = File.createTempFile("tmp", "tst");
        testFolder = new File(file.getParentFile(), "TestFolder/");
        if (testFolder.exists()) {
            FileUtils.deleteDirectory(testFolder);
        }
        testFolder.mkdir();

    }

    /**
     * Удаление тестового каталога
     */
    @After
    public void removeTestFolder() throws IOException {
        if (testFolder != null && testFolder.exists()) {
            FileUtils.deleteDirectory(testFolder);
        }

    }

    /**
     * Тест создания пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateFromFolder() throws Exception {
        //GIVEN
        File idFolder = new File(testFolder, "NUnit/");
        File versionFolder = new File(idFolder, "2.5.9.10348/");
        versionFolder.mkdirs();
        File targetFile = new File(versionFolder, "NUnit.2.5.9.10348.nupkg");
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), new FileOutputStream(targetFile).getChannel());
        File hashFile = new File(versionFolder, MavenNupkg.HASH_FILE_NAME);
        FileWriter fileWriter = new FileWriter(hashFile);
        fileWriter.write("kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==");
        fileWriter.flush();
        //WHEN
        MavenNupkg mavenNupkg = new MavenNupkg(versionFolder);
        //THEN
        assertEquals("Идентификатор пакета", "NUnit", mavenNupkg.getId());
        assertEquals("Версия пакета", Version.parse("2.5.9.10348"), mavenNupkg.getVersion());
        assertEquals("Хеш файла", "kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==", mavenNupkg.getHash().toString());
    }

    //THEN
    /**
     * Создание пакета из файла запрещено. Можно создавать только из каталога
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test(expected = NugetFormatException.class)
    public void testCreateFromFile() throws Exception {
        //GIVEN
        File packageFile = File.createTempFile("tmp", "tmp");
        FileOutputStream fileOutputStream = new FileOutputStream(packageFile);
        fileOutputStream.write(new byte[]{1, 1, 1});
        //WHEN
        MavenNupkg result = new MavenNupkg(packageFile);
        System.out.println(result);
    }

    //THEN
    /**
     * Создание пакета из каталога, в котором нет файла пакета запрещено
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test(expected = NugetFormatException.class)
    public void testCreateWhereNoPackageFile() throws Exception {
        //GIVEN
        File idFolder = new File(testFolder, "NUnit/");
        File versionFolder = new File(idFolder, "2.5.9.10348/");
        versionFolder.mkdirs();
        //WHEN
        MavenNupkg result = new MavenNupkg(versionFolder);
        System.out.println(result);
    }
    //THEN

    /**
     * Создание пакета из каталога, в котором нет файла хеша
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test(expected = NugetFormatException.class)
    public void testCreateWhereNoHasheFile() throws Exception {
        //GIVEN
        File idFolder = new File(testFolder, "NUnit/");
        File versionFolder = new File(idFolder, "2.5.9.10348/");
        versionFolder.mkdirs();
        File targetFile = new File(versionFolder, "NUnit.2.5.9.10348.nupkg");
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), new FileOutputStream(targetFile).getChannel());
        //WHEN
        MavenNupkg result = new MavenNupkg(versionFolder);
        System.out.println(result);
    }
}

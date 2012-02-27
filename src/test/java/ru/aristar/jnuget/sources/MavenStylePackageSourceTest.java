package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
}

package ru.aristar.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.Hash;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class NugetClientTest {

    /**
     * Каталог с пакетами
     */
    private static File packageFolder;

    /**
     * Инициализация настроек интеграционных тестов
     *
     * @throws IOException ошибка копирования файла
     */
    @BeforeClass
    public static void Setup() throws IOException {
        String homeFolderName = System.getProperty("nuget.home");
        File testFolder = new File(homeFolderName);
        testFolder.mkdirs();
        packageFolder = new File(testFolder, "Packages");
        packageFolder.mkdirs();
        File packageFile = new File(packageFolder, "NUnit.2.5.9.10348.nupkg");
        try (InputStream inputStream = RssIntegrationTests.class.getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
                FileOutputStream outputStream = new FileOutputStream(packageFile)) {
            TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), outputStream.getChannel());
        }
    }

    /**
     * Удаление тестового каталога
     *
     * @throws IOException ошибка удаления тестового каталога
     */
    @AfterClass
    public static void TearDown() throws IOException {
        if (packageFolder != null && packageFolder.exists()) {
            FileUtils.deleteDirectory(packageFolder);
        }
    }

    /**
     * Получение потока с данными пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetPackageStream() throws Exception {
        //GIVEN
        NugetClient nugetClient = new NugetClient();
        nugetClient.setUrl("http://localhost:8088/nuget");
        //WHEN
        InputStream result = nugetClient.getPackage(InputStream.class, "NUnit", "2.5.9.10348");
        TempNupkgFile nupkgFile = new TempNupkgFile(result);
        //THEN
        assertEquals("Идентификатор пакета", "NUnit", nupkgFile.getId());
        assertEquals("Версия пакета", Version.parse("2.5.9.10348"), nupkgFile.getVersion());
        assertEquals("HASH пакета", Hash.parse("kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA=="), nupkgFile.getHash());
    }
}

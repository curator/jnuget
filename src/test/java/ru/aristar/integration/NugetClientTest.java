package ru.aristar.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.Hash;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.rss.PackageFeed;

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
     * URL тестового хранилища
     */
    private static final String TEST_PACKAGESOURCE_URL = "http://localhost:8088/storages/DefaultSource/";

    /**
     * Инициализация настроек интеграционных тестов
     *
     * @throws IOException ошибка копирования файла
     */
    @BeforeClass
    public static void Setup() throws IOException {
        String homeFolderName = System.getProperty("nuget.home");
        if (homeFolderName == null) {
            homeFolderName = System.getProperty("user.dir") + "/target/WorkFolder/";
        }
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
        nugetClient.setUrl(TEST_PACKAGESOURCE_URL);
        //WHEN
        TempNupkgFile result = nugetClient.getPackage("NUnit", Version.parse("2.5.9.10348"));
        //THEN
        assertEquals("Идентификатор пакета", "NUnit", result.getId());
        assertEquals("Версия пакета", Version.parse("2.5.9.10348"), result.getVersion());
        assertEquals("HASH пакета", Hash.parse("kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA=="), result.getHash());
    }

    /**
     * Получение списка пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetAllPackages() throws Exception {
        //GIVEN
        NugetClient nugetClient = new NugetClient();
        nugetClient.setUrl(TEST_PACKAGESOURCE_URL);
        //WHEN
        PackageFeed result = nugetClient.getPackages(null, null, null, null, null);
        //THEN
        assertEquals("Количество пакетов", 1, result.getEntries().size());
        PackageEntry entry = result.getEntries().get(0);
        assertThat("Версия пакета", entry.getProperties().getVersion(), is(equalTo(Version.parse("2.5.9.10348"))));
        assertThat("HASH пакета", entry.getProperties().getPackageHash(), is(equalTo("kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==")));
        assertThat("Идентификатор пакета", entry.getTitle(), is(equalTo("NUnit")));
    }

    /**
     * Проверка получения количества пакетов
     *
     * @throws IOException ошибка чтения из сокета
     * @throws URISyntaxException некорректный URI ресурса
     */
    @Test
    public void testGetPackageCount() throws IOException, URISyntaxException {
        //GIVEN
        NugetClient nugetClient = new NugetClient();
        nugetClient.setUrl(TEST_PACKAGESOURCE_URL);
        //WHEN
        int result = nugetClient.getPackageCount(false);
        //THEN
        assertThat(result, is(equalTo(1)));
    }

    /**
     * Проверка получения количества последних версий пакета (пакетов с
     * уникальными идентификаторами)
     *
     * @throws IOException ошибка чтения из сокета
     * @throws URISyntaxException некорректный URI ресурса
     */
    @Test
    public void testGetLastVersionPackageCount() throws IOException, URISyntaxException {
        //GIVEN
        NugetClient nugetClient = new NugetClient();
        nugetClient.setUrl(TEST_PACKAGESOURCE_URL);
        //WHEN
        int result = nugetClient.getPackageCount(true);
        //THEN
        assertThat(result, is(equalTo(1)));
    }
}

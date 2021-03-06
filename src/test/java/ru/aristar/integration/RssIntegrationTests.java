package ru.aristar.integration;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 * Интеграционные тесты сайта
 *
 * @author sviridov
 */
public class RssIntegrationTests {

    /**
     * Каталог с пакетами
     */
    private static File packageFolder;
    private static final String STORAGE_URL = "http://localhost:8088/storages/DefaultSource";

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
     * Корневой URL содержит страницу с общей информацией
     *
     * @throws IOException ошибка чтения данных из сокета
     * @throws SAXException ошибка распознования XML
     */
    @Test
    public void testGetRootPage() throws IOException, SAXException {
        //GIVEN
        WebConversation webConversation = new WebConversation();
        //WHEN
        WebResponse response = webConversation.getResponse("http://localhost:8088");
        //THEN
        assertTrue(response.getText().contains("to view manager."));
    }

    /**
     * Тест получения заголовка RSS со списком пакетов
     *
     * @throws IOException ошибка чтения данных из сокета
     * @throws SAXException ошибка распознования XML
     */
    @Test
    public void testGetPackageList() throws IOException, SAXException {
        //GIVEN
        WebConversation webConversation = new WebConversation();
        //WHEN
        WebResponse response = webConversation.getResponse(STORAGE_URL + "/Packages");
        //THEN
        assertTrue(response.getText().contains("type=\"text\">Packages<"));
    }

    /**
     * Тест получения корневого XML
     *
     * @throws IOException ошибка чтения данных из сокета
     * @throws SAXException ошибка распознования XML
     */
    @Test
    public void testGetMainXml() throws IOException, SAXException {
        //GIVEN
        WebConversation webConversation = new WebConversation();
        //WHEN
        WebResponse response = webConversation.getResponse(STORAGE_URL);
        //THEN
        assertTrue(response.getText().contains("xml:base=\"" + STORAGE_URL + "\""));
        assertTrue(response.getText().contains("title>Default<"));
        assertTrue(response.getText().contains("href=\"Packages\""));
        assertTrue(response.getText().contains("title>Packages<"));

    }

    /**
     * Тест получения записи о пакете
     *
     * @throws IOException ошибка чтения данных из сокета
     * @throws SAXException ошибка распознования XML
     */
    @Test
    public void testGetPackageEntry() throws IOException, SAXException {
        //GIVEN
        WebConversation webConversation = new WebConversation();
        //WHEN
        WebResponse response = webConversation.getResponse(STORAGE_URL + "/Packages");
        //THEN
        assertTrue(response.getText().contains("Packages(Id='NUnit',Version='2.5.9.10348')"));
    }

    /**
     * Тест получения количества пакетов
     *
     * @throws IOException ошибка чтения данных из сокета
     * @throws SAXException ошибка распознования XML
     */
    @Test
    public void testGetPackageCount() throws IOException, SAXException {
        //GIVEN
        WebConversation webConversation = new WebConversation();
        //WHEN
        WebResponse response = webConversation.getResponse(STORAGE_URL + "/Search()/$count?$filter=IsLatestVersion&searchTerm=''&targetFramework='net20'");
        //THEN
        assertEquals("В хранилище должно быть определенное количество пакетов", "1", response.getText());
    }
}

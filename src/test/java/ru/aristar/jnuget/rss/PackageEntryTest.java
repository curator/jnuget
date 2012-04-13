package ru.aristar.jnuget.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import static ru.aristar.common.TestHelper.assertOneOfAreEquals;
import ru.aristar.jnuget.NugetContext;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 * Тесты для RSS вложения с информацией о пакете
 *
 * @author sviridov
 */
public class PackageEntryTest {

    /**
     * Проверка создания RSS вложения на основе данных пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testConvertNuPkgToEntry() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        final Date date = new Date();
        TempNupkgFile nupkgFile = new TempNupkgFile(inputStream, date);
        //WHEN
        NugetContext context = new NugetContext(new URI("http://localhost:8090/"));
        PackageEntry entry = context.createPackageEntry(nupkgFile);
        //THEN
        assertEquals("Идентификатор пакета",
                "http://localhost:8090/nuget/Packages(Id='NUnit',Version='2.5.9.10348')",
                entry.getId());
        assertEquals("Название пакета", "NUnit", entry.getTitle());
        assertEquals("Описание пакета", null, entry.getSummary());
        assertEquals("Дата обновления пакета", date, entry.getUpdated());
        assertNotNull("Автор пакета", entry.getAuthor());
        assertEquals("Автор пакета", "NUnit", entry.getAuthor().getName());
        assertEquals("Количество ссылок", 2, entry.getLinks().size());
        assertEquals("Тип ссылки", "Package", entry.getLinks().get(0).getTitle());
        assertEquals("Тип ссылки", "Package", entry.getLinks().get(1).getTitle());
        List<String> rels = Arrays.asList(entry.getLinks().get(0).getRel(), entry.getLinks().get(1).getRel());
        assertOneOfAreEquals("Ссылочный тип", "edit-media", rels);
        assertOneOfAreEquals("Ссылочный тип", "edit", rels);
        List<String> hrefs = Arrays.asList(entry.getLinks().get(0).getHref(), entry.getLinks().get(1).getHref());
        assertOneOfAreEquals("Ссылка", "Packages(Id='NUnit',Version='2.5.9.10348')/$value", hrefs);
        assertOneOfAreEquals("Ссылка", "Packages(Id='NUnit',Version='2.5.9.10348')", hrefs);
        assertEquals("Категория", "NuGet.Server.DataServices.Package", entry.getCategory().getTerm());
        assertEquals("Категория схема", "http://schemas.microsoft.com/ado/2007/08/dataservices/scheme", entry.getCategory().getScheme());
        assertNull("Категория источник", entry.getCategory().getSrc());
        assertNull("Категория тип", entry.getCategory().getType());
        assertNull("Контент", entry.getContent().getTerm());
        assertNull("Контент схема", entry.getContent().getScheme());
        assertEquals("Контент источник", "http://localhost:8090/download/NUnit/2.5.9.10348", entry.getContent().getSrc());
        assertEquals("Контент тип", "application/zip", entry.getContent().getType());
    }

    /**
     * Проверка свойств вложения RSS, созданных на основе пакета
     *
     * @throws IOException ошибка чтения тестового пакета
     * @throws NugetFormatException тестовый пакет не соответствует стандарту
     * @throws URISyntaxException тестовый URL ресура не соответствует формату
     * @throws NoSuchAlgorithmException в системе не установлены библиотеки
     * вычисления HASH значения
     */
    @Test
    public void testConvertNupkgToEntryProperties() throws IOException,
            NugetFormatException, URISyntaxException, NoSuchAlgorithmException {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        final Date date = new Date();
        TempNupkgFile nupkgFile = new TempNupkgFile(inputStream, date);
        //WHEN
        NugetContext context = new NugetContext(new URI("http://localhost:8090/"));
        EntryProperties properties = context.createPackageEntry(nupkgFile).getProperties();
        //THEN
        assertThat("Свойства RSS вложения созданы", properties, is(notNullValue()));
        assertThat("Дата публикации пакета", properties.getPublished(), is(equalTo(date)));
        assertThat("Хеш пакета", properties.getPackageHash(), is(equalTo(nupkgFile.getHash().toString())));
        assertThat("Размер пакета", properties.getPackageSize(), is(equalTo(nupkgFile.getSize())));
    }
}

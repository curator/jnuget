package ru.aristar.jnuget.rss;

import java.io.InputStream;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author sviridov
 */
public class EntryPropertiesTest {

//TODO Language 
//TODO Published 2011-09-23T05:18:55.5327281Z
//TODO Price 0
//TODO Dependencies
//TODO PackageHash CoknSJBGJ7kao2P6y9E9BuL1IkhP5LLhZ+ImtsgdxzFDpjs0QtRVOV8kxysakJu3cvw5O0hImcnVloCaQ9+Nmg==
//TODO PackageSize 214905
//TODO ExternalPackageUri 
//TODO Categories 
//TODO Copyright 
//TODO PackageType 
//TODO Tags Unit test 
//IsLatestVersion true
//TODO Summary     
    @Test
    public void testConvertNuspecToEntryProperties() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.nuspec.xml");
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        EntryProperties properties = new EntryProperties();
        //WHEN        
        properties.setNuspec(nuspecFile);
        properties.setIsLatestVersion(true);
        //THEN
        assertEquals("Версия пакета", new Version(2, 5, 9, "10348"), properties.getVersion());        
        //**************************************
        assertEquals("Описание пакета", "Пакет модульного тестирования", properties.getDescription());          
        //**************************************
        assertEquals("Версия пакета является последней", "true", properties.getIsLatestVersion().getValue());
        //assertEquals("Заголовок значение", "", properties.getTitle().getValue());
        fail("Тест не полностью реализован");
    }

    @Test
    public void testParseProperties() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.properties.xml");
        //WHEN
        EntryProperties entryProperties = EntryProperties.parse(inputStream);
        //THEN
        assertEquals("Версия пакета", "2.5.9.10348", entryProperties.getVersion().toString());
        assertNotNull("Заголовок", entryProperties.getTitle());
        assertEquals("Заголовок значение", "", entryProperties.getTitle().getValue());
        assertTrue("Заголовок nullable", entryProperties.getTitle().getNullable());

        assertNotNull("URL иконки", entryProperties.getIconUrl());
        assertEquals("URL иконки значение", "", entryProperties.getIconUrl().getValue());
        assertTrue("URL иконки nullable", entryProperties.getIconUrl().getNullable());

        assertNotNull("URL лицензии", entryProperties.getLicenseUrl());
        assertEquals("URL лицензии значение", "", entryProperties.getLicenseUrl().getValue());
        assertTrue("URL лицензии nullable", entryProperties.getLicenseUrl().getNullable());

        assertNotNull("URL проекта", entryProperties.getProjectUrl());
        assertEquals("URL проекта значение", "", entryProperties.getProjectUrl().getValue());
        assertTrue("URL проекта nullable", entryProperties.getProjectUrl().getNullable());

        assertNotNull("URL отчета", entryProperties.getReportAbuseUrl());
        assertEquals("URL отчета значение", "", entryProperties.getReportAbuseUrl().getValue());
        assertTrue("URL отчета nullable", entryProperties.getReportAbuseUrl().getNullable());

        assertNotNull("Количество загрузок пакета", entryProperties.getDownloadCount());
        assertEquals("Количество загрузок пакета значение", "-1", entryProperties.getDownloadCount().getValue());
        assertEquals("Количество загрузок пакета nullable", MicrosoftTypes.Int32, entryProperties.getDownloadCount().getType());

        assertEquals("Количество загрузок версий",
                new MicrosoftDatasetElement(null, MicrosoftTypes.Int32, "-1"),
                entryProperties.getVersionDownloadCount());

        assertEquals("Рейтинг (количество)",
                new MicrosoftDatasetElement(null, MicrosoftTypes.Int32, "0"),
                entryProperties.getRatingsCount());

        assertEquals("Рейтинг версии (количество)",
                new MicrosoftDatasetElement(null, MicrosoftTypes.Int32, "-1"),
                entryProperties.getVersionRatingsCount());

        assertEquals("Рейтинг",
                new MicrosoftDatasetElement(null, MicrosoftTypes.Double, "-1"),
                entryProperties.getRating());

        assertEquals("Рейтинг версии",
                new MicrosoftDatasetElement(null, MicrosoftTypes.Double, "-1"),
                entryProperties.getVersionRating());

        assertEquals("Требуется лицензия",
                new MicrosoftDatasetElement(null, MicrosoftTypes.Boolean, "false"),
                entryProperties.getRequireLicenseAcceptance());

        assertEquals("Описание пакета", "Пакет модульного тестирования",
                entryProperties.getDescription());

        assertEquals("Замечания крелизу",
                new MicrosoftDatasetElement(Boolean.TRUE, null, ""),
                entryProperties.getReleaseNotes());

        //*****************************************************
        assertEquals("Это последняя версия",
                new MicrosoftDatasetElement(null, MicrosoftTypes.Boolean, "true"),
                entryProperties.getIsLatestVersion());

        fail("Тест не дописан");
    }
}

package ru.aristar.jnuget.rss;

import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author sviridov
 */
public class EntryPropertiesTest {

//TODO Title 
//TODO IconUrl 
//TODO LicenseUrl 
//TODO ProjectUrl 
//TODO ReportAbuseUrl 
//TODO DownloadCount -1
//TODO VersionDownloadCount -1
//TODO RatingsCount 0
//TODO VersionRatingsCount -1
//TODO Rating -1
//TODO VersionRating -1
//TODO RequireLicenseAcceptance false
//TODO DescriptionПакет модульного тестирования
//TODO ReleaseNotes 
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
//TODO IsLatestVersion true
//TODO Summary     
    @Test
    public void testConvertNuspecToEntryProperties() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.nuspec.xml");
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        EntryProperties properties = new EntryProperties();
        //WHEN        
        properties.setNuspec(nuspecFile);
        //THEN
        assertEquals("Версия пакета", new Version(2, 5, 9, "10348"), properties.getVersion());
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

        fail("Тест не дописан");
    }
}

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

    /*
            Title 
            IconUrl 
            LicenseUrl 
            ProjectUrl 
            ReportAbuseUrl 
            DownloadCount -1
            VersionDownloadCount -1
            RatingsCount 0
            VersionRatingsCount -1
            Rating -1
            VersionRating -1
            RequireLicenseAcceptance false
            DescriptionПакет модульного тестирования
            ReleaseNotes 
            Language 
            Published 2011-09-23T05:18:55.5327281Z
            Price 0
            Dependencies
            PackageHash CoknSJBGJ7kao2P6y9E9BuL1IkhP5LLhZ+ImtsgdxzFDpjs0QtRVOV8kxysakJu3cvw5O0hImcnVloCaQ9+Nmg==
            PackageSize 214905
            ExternalPackageUri 
            Categories 
            Copyright 
            PackageType 
            Tags Unit test 
            IsLatestVersion true
            Summary     
     */
    

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
}

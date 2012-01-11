package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.files.NupkgFile;

/**
 *
 * @author sviridov
 */
public class PackageEntryTest {

    //TODO    <id>http://localhost:8090/nuget/nuget/Packages(Id='NUnit',Version='2.5.9.10348')</id>
    //TODO    <link rel="edit-media" title="Package" href="Packages(Id='NUnit',Version='2.5.9.10348')/$value" />
    //TODO    <link rel="edit" title="Package" href="Packages(Id='NUnit',Version='2.5.9.10348')" />
    //TODO    <category term="NuGet.Server.DataServices.Package" scheme="http://schemas.microsoft.com/ado/2007/08/dataservices/scheme" />
    //TODO    <content type="application/zip" src="http://localhost:8090/nuget/download/NUnit/2.5.9.10348" />    
    @Test
    public void testConvertNuPkgToEntry() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        Date date = new Date();
        NupkgFile nupkgFile = new NupkgFile(inputStream, date);
        //WHEN
        PackageEntry entry = new PackageEntry(nupkgFile);
        //THEN
        assertEquals("Название пакета", "NUnit", entry.getTitle());
        assertEquals("Описание пакета", null, entry.getSummary());
        assertEquals("Дата обновления пакета", date, entry.getUpdated());
        assertNotNull("Автор пакета", entry.getAuthor());
        assertEquals("Автор пакета", "NUnit", entry.getAuthor().getName());

        fail("Тест не реализован");
    }
}

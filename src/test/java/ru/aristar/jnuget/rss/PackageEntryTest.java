package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import ru.aristar.jnuget.files.NupkgFile;

/**
 *
 * @author sviridov
 */
public class PackageEntryTest {

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
        assertEquals("Дата обновления пакета", date, entry.getUpdated());
        fail("Тест не реализован");
    }
}

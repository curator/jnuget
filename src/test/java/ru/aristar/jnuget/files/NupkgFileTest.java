package ru.aristar.jnuget.files;

import java.io.InputStream;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.aristar.jnuget.NuspecFileTest;

/**
 *
 * @author sviridov
 */
public class NupkgFileTest {

    @Test
    public void testReadNuspeck() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        //WHEN
        NupkgFile nupkgFile = new NupkgFile(inputStream, new Date());
        NuspecFile result = nupkgFile.getNuspecFile();
        //THEN
        assertEquals("Описание пакета", "Пакет модульного тестирования", result.getDescription());
    }

    @Test
    public void testGetFileName() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        //WHEN
        NupkgFile nupkgFile = new NupkgFile(inputStream, new Date());
        //THEN
        assertEquals("Имя файла", "NUnit.2.5.9.10348.nupkg", nupkgFile.getFileName());
    }
}

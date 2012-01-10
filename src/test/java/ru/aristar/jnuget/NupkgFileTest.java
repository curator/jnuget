package ru.aristar.jnuget;

import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

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
        NupkgFile nupkgFile = new NupkgFile(inputStream);
        NuspecFile result = nupkgFile.getNuspecFile();
        //THEN
        assertEquals("Описание пакета", "Пакет модульного тестирования", result.getDescription());
    }
}

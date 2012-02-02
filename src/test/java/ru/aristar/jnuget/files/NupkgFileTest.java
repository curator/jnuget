package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
        Nupkg nupkgFile = new TempNupkgFile(inputStream, new Date());
        NuspecFile result = nupkgFile.getNuspecFile();
        //THEN
        assertEquals("Описание пакета", "Пакет модульного тестирования", result.getDescription());
    }

    @Test
    public void testGetFileName() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        //WHEN
        Nupkg nupkgFile = new TempNupkgFile(inputStream, new Date());
        //THEN
        assertEquals("Имя файла", "NUnit.2.5.9.10348.nupkg", nupkgFile.getFileName());
    }

    @Test
    public void testGetHash() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        File tempFile = File.createTempFile("tmp", "nupkg");
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) >= 0) {
            fileOutputStream.write(buffer, 0, len);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        //WHEN
        Nupkg nupkgFile = new TempNupkgFile(new FileInputStream(tempFile));
        //THEN
        assertEquals("Хеш файла", "kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==", nupkgFile.getHash().toString());
    }
}

package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class NupkgTest {

    @Test
    public void testParseWithFullVersion() throws Exception {
        // Given
        final String idStr = "NUnit";
        final String versionStr = "2.5.9.10348";
        final String filename = String.format("%s.%s.nupkg", idStr, versionStr);
        // When
        Nupkg result = new ClassicNupkg(new File(filename));
        // Then
        Assert.assertEquals("Неправильный id пакета", idStr, result.getId());
        Assert.assertEquals("Неправильный версия пакета", Version.parse(versionStr), result.getVersion());
        Assert.assertEquals("Неправильное имя файла", "NUnit.2.5.9.10348.nupkg", result.getFileName());
    }

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

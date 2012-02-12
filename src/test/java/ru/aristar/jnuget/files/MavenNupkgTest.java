package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 * Тест пакета для хранилища в стиле Maven
 *
 * @author sviridov
 */
public class MavenNupkgTest {

    /**
     * Тест создания пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateFromFolder() throws Exception {
        //GIVEN
        File rootFolder = File.createTempFile("tmp", "tmp").getParentFile();
        File idFolder = new File(rootFolder, "NUnit/");
        File versionFolder = new File(idFolder, "2.5.9.10348/");
        versionFolder.mkdirs();
        File targetFile = new File(versionFolder, "NUnit.2.5.9.10348.nupkg");
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), new FileOutputStream(targetFile).getChannel());
        //WHEN
        MavenNupkg mavenNupkg = new MavenNupkg(versionFolder);
        //THEN
        assertEquals("Идентификатор пакета", "NUnit", mavenNupkg.getId());
        assertEquals("Версия пакета", Version.parse("2.5.9.10348"), mavenNupkg.getVersion());
        assertEquals("Хеш файла", "kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==", mavenNupkg.getHash().toString());
    }
}

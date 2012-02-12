package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class MavenStylePackageSourceTest {

    /**
     * Тестовая папка с пакетами
     */
    private static File testFolder;

    /**
     * Создание тестового каталога и наполнение его файлами
     *
     * @throws IOException
     */
    @BeforeClass
    public static void createTestFolder() throws IOException {
        File file = File.createTempFile("tmp", "tst");
        Pattern pattern = Pattern.compile("/(.+)\\.(\\d+\\.\\d+\\.\\d+\\.\\d+)\\.nupkg", Pattern.CASE_INSENSITIVE);
        testFolder = new File(file.getParentFile(), "TestFolder/");
        testFolder.mkdir();
        String[] resources = new String[]{"/NUnit.2.5.9.10348.nupkg"};
        for (String resource : resources) {
            Matcher matcher = pattern.matcher(resource);
            matcher.matches();
            File packageFolder = new File(testFolder, matcher.group(1) + "/");
            packageFolder.mkdirs();
            File versionFolder = new File(packageFolder, matcher.group(2) + "/");
            versionFolder.mkdirs();
            File targetFile = new File(versionFolder, resource.substring(1));
            try (ReadableByteChannel sourceChannel = Channels.newChannel(MavenStylePackageSourceTest.class.getResourceAsStream(resource));
                    FileChannel targetChannel = new FileOutputStream(targetFile).getChannel();) {
                TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
            }
        }
    }

    /**
     * Удаление тестового каталога
     */
    @AfterClass
    public static void removeTestFolder() {
        if (testFolder != null && testFolder.exists()) {
            testFolder.delete();
        }

    }

    @Test
    public void testGetPackagesById() {
        MavenStylePackageSource packageSource = new MavenStylePackageSource(testFolder);
        Collection<Nupkg> result = packageSource.getPackages("NUnit");
        assertNotNull("Коллекция пакетов", result);
        assertEquals("Пакетов в коллекции", 1, result.size());
        assertEquals("Идентификатор пакета", "NUnit", result.iterator().next().getId());
    }
}

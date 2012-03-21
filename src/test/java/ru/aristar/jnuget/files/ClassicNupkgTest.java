package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.sources.FilePackageSourceTest;

/**
 *
 * @author sviridov
 */
public class ClassicNupkgTest {

    /**
     * Тестовая папка с пакетами
     */
    private static File testFolder;
    /**
     * Список имен ресурсов с пакетами
     */
    private final static String[] resources = new String[]{"/NUnit.2.5.9.10348.nupkg"};
    /**
     * Список тестовых файлов пакетов
     */
    private static File[] packageFiles = new File[resources.length];

    /**
     * Создание тестового каталога и наполнение его файлами
     *
     * @throws IOException
     */
    @BeforeClass
    public static void createTestFolder() throws IOException {
        File file = File.createTempFile("tmp", "tst");
        testFolder = new File(file.getParentFile(), "TestFolder/");
        testFolder.mkdir();
        for (int i = 0; i < resources.length; i++) {
            String resource = resources[i];
            InputStream inputStream = FilePackageSourceTest.class.getResourceAsStream(resource);
            File targetFile = new File(testFolder, resource.substring(1));
            try (ReadableByteChannel source = Channels.newChannel(inputStream);
                    FileChannel target = new FileOutputStream(targetFile).getChannel()) {
                TempNupkgFile.fastChannelCopy(source, target);
            }
            packageFiles[i] = targetFile;
        }
    }

    /**
     * Удаление тестового каталога
     */
    @AfterClass
    public static void removeTestFolder() throws IOException {
        if (testFolder != null && testFolder.exists()) {
            FileUtils.deleteDirectory(testFolder);
        }

    }

    /**
     * Проверка чтения спецификации из файла пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testReadNuspecFromPackage() throws Exception {
        //WHEN
        ClassicNupkg classicNupkg = new ClassicNupkg(packageFiles[0]);
        //THEN
        assertNotNull("Из файла должна быть прочитана спецификация", classicNupkg.getNuspecFile());
    }
}

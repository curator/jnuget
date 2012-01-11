package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.files.NupkgFile;

/**
 *
 * @author sviridov
 */
public class FilePackageSourceTest {

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
        testFolder = new File(file.getParentFile(), "TestFolder/");
        testFolder.mkdir();
        String[] resources = new String[]{"/NUnit.2.5.9.10348.nupkg"};
        for (String resource : resources) {
            InputStream inputStream = FilePackageSourceTest.class.getResourceAsStream(resource);
            File targetFile = new File(testFolder, resource.substring(1));
            try (FileOutputStream targetStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) >= 0) {
                    targetStream.write(buffer, 0, len);
                }
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

    /**
     * Проверка чтения пакетов из каталога
     */
    @Test
    public void testReadFilesFromFolder() {
        //GIVEN
        FilePackageSource packageSource = new FilePackageSource(testFolder);
        //WHEN
        Collection<NupkgFile> packages = packageSource.getPackages();
        //THEN
        assertEquals("Прочитано файлов", 1, packages.size());
        assertEquals("Идентификатор пакета", "NUnit", packages.iterator().next().getNuspecFile().getId());
    }
}

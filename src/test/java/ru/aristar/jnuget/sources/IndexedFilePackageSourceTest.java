package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.jmock.Mockery;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.ClassicNupkg;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class IndexedFilePackageSourceTest {

    /**
     * Тестовая папка с пакетами
     */
    private static File testFolder;
    /**
     * Контекст заглушек
     */
    private Mockery context = new Mockery();

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
            File targetFile = new File(testFolder, resource.substring(1));
            try (ReadableByteChannel sourceChannel = Channels.newChannel(FilePackageSourceTest.class.getResourceAsStream(resource));
                    FileChannel targetChannel = new FileOutputStream(targetFile).getChannel();) {
                TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
            }
        }
    }

    /**
     * Получение списка всех пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetAllPackages() throws Exception {
        //GIVEN
        IndexedFilePackageSource packageSource = new IndexedFilePackageSource();
        packageSource.setFolderName(testFolder.getAbsolutePath()).join();
        //WHEN
        Nupkg[] result = packageSource.getPackages().toArray(new Nupkg[0]);
        //THEN
        assertEquals("Число пакетов в хранилище", 1, result.length);
    }

    /**
     * Проверка помещения файла в хранилище
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testPushPackages() throws Exception {
        //GIVEN
        File file = File.createTempFile("tmp", "tst");
        File localTestFolder = new File(file.getParentFile(), "LocalTestFolder/");
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile nupkgFile = new TempNupkgFile(inputStream);
        IndexedFilePackageSource packageSource = new IndexedFilePackageSource();
        packageSource.setPushStrategy(new SimplePushStrategy(true));
        packageSource.setFolderName(localTestFolder.getAbsolutePath()).join();
        //WHEN
        packageSource.pushPackage(nupkgFile, null);
        //THEN
        Nupkg nupkg = packageSource.getPackage("NUnit", Version.parse("2.5.9.10348"));
        assertNotNull(nupkg);
        assertEquals(ClassicNupkg.class, nupkg.getClass());
        ClassicNupkg classicNupkg = (ClassicNupkg) nupkg;
        assertEquals(localTestFolder, classicNupkg.getLocalFile().getParentFile());
    }
}

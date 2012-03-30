package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.ClassicNupkg;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.sources.push.SimplePushStrategy;

/**
 *
 * @author sviridov
 */
public class IndexedPackageSourceTest {

    /**
     * Тестовая папка с пакетами
     */
    private static File testFolder;

    /**
     * Удаление тестового каталога
     *
     * @throws IOException ошибка удаления тестового каталога
     */
    @AfterClass
    public static void removeTestFolder() throws IOException {
        if (testFolder != null && testFolder.exists()) {
            FileUtils.deleteDirectory(testFolder);
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
        removeTestFolder();
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
        IndexedPackageSource packageSource = new IndexedPackageSource();
        FilePackageSource filePackageSource = new FilePackageSource(testFolder);
        packageSource.setUnderlyingSource(filePackageSource).join();
        //WHEN
        Collection<Nupkg> result = packageSource.getPackages();
        //THEN
        assertEquals("Число пакетов в хранилище", 1, result.size());
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
        if (localTestFolder.exists()) {
            FileUtils.deleteDirectory(localTestFolder);
        }
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        try (TempNupkgFile nupkgFile = new TempNupkgFile(inputStream)) {
            IndexedPackageSource packageSource = new IndexedPackageSource();
            FilePackageSource filePackageSource = new FilePackageSource(localTestFolder);
            packageSource.setUnderlyingSource(filePackageSource).join();
            packageSource.setPushStrategy(new SimplePushStrategy(true));
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
}

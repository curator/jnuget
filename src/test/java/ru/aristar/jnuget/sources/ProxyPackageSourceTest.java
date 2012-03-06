package ru.aristar.jnuget.sources;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Expectation;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.MavenNupkg;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class ProxyPackageSourceTest {

    /**
     * Тестовая папка с пакетами
     */
    private static File testFolder;
    /**
     * Контекст для создания заглушек
     */
    private Mockery context = new Mockery() {

        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

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
        String resource = "/NUnit.2.5.9.10348.nupkg";

        Matcher matcher = pattern.matcher(resource);
        matcher.matches();
        File packageFolder = new File(testFolder, matcher.group(1) + "/");
        packageFolder.mkdirs();
        File versionFolder = new File(packageFolder, matcher.group(2) + "/");
        versionFolder.mkdirs();
        File targetFile = new File(versionFolder, resource.substring(1));
        try (ReadableByteChannel sourceChannel = Channels.newChannel(MavenStylePackageSourceTest.class.getResourceAsStream(resource));
                FileChannel targetChannel = new FileOutputStream(targetFile).getChannel()) {
            TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
        }
        File hashfile = new File(versionFolder, MavenNupkg.HASH_FILE_NAME);
        try (FileWriter fileWriter = new FileWriter(hashfile)) {
            fileWriter.write("kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==");
        }
        File nuspecFile = new File(versionFolder, MavenNupkg.NUSPEC_FILE_NAME);
        try (ReadableByteChannel sourceChannel = Channels.newChannel(MavenStylePackageSourceTest.class.getResourceAsStream("/NUnit.nuspec.xml"));
                FileChannel targetChannel = new FileOutputStream(nuspecFile).getChannel()) {
            TempNupkgFile.fastChannelCopy(sourceChannel, targetChannel);
        }
    }

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
     * Получение списка пакетов с указанным идентификатором
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetPackagesByIdFromLocalStorage() throws Exception {
        //GIVEN
        ProxyPackageSource packageSource = new ProxyPackageSource();
        packageSource.setFolderName(testFolder.getAbsolutePath());
        packageSource.remoteSource = null;
        //WHEN
        Collection<Nupkg> result = packageSource.getPackages("NUnit");
        //THEN
        assertNotNull("Коллекция пакетов", result);
        assertEquals("Пакетов в коллекции", 1, result.size());
        Nupkg resultNupkg = result.iterator().next();
        assertEquals("Идентификатор пакета", "NUnit", resultNupkg.getId());
        assertEquals("Хеш объекта", "kDPZtMu1BOZerHZvsbPnj7DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZjPRJA==", resultNupkg.getHash().toString());
    }

    /**
     * Тест публикации пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testPushPackage() throws Exception {
        //GIVEN
        ProxyPackageSource packageSource = new ProxyPackageSource();
        //WHEN
        boolean result = packageSource.pushPackage(null, null);
        //THEN
        assertFalse("Запрещена публикация пакетов", result);
    }

    /**
     * Получение пакета с заданными идентификатором и версией из удаленного
     * хранилища и сохранение его в локальном
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetPackageStream() throws Exception {
        //GIVEN
        FileUtils.deleteDirectory(new File(testFolder, "NUnit"));
        final RemotePackageSource remotePackageSource = context.mock(RemotePackageSource.class);
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        try (final TempNupkgFile tempNupkgFile = new TempNupkgFile(inputStream)) {
            context.checking(new Expectations() {

                {
                    atLeast(1).of(remotePackageSource).getPackage("NUnit", Version.parse("2.5.9.10348"));
                    will(returnValue(tempNupkgFile));
                }
            });
            ProxyPackageSource packageSource = new ProxyPackageSource();
            packageSource.setFolderName(testFolder.getAbsolutePath());
            packageSource.remoteSource = remotePackageSource;
            //WHEN
            MavenNupkg result = packageSource.getPackage("NUnit", Version.parse("2.5.9.10348"));
            //THEN
            assertTrue("Создан каталог в хранилище", new File(testFolder, "NUnit".toLowerCase()).exists());
            assertEquals("Идентификатор пакета", "NUnit", result.getId());
            assertEquals("Версия пакета", Version.parse("2.5.9.10348"), result.getVersion());
        }
    }
}

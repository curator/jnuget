package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.nuspec.NuspecFile;

/**
 *
 * @author Unlocker
 */
public class MavenNupkg extends ClassicNupkg implements Nupkg {

    /**
     * Название файла с контрольной суммой
     */
    public static final String HASH_FILE_NAME = "hash.sha512";
    /**
     * Название извлеченного файла nuspec
     */
    public static final String NUSPEC_FILE_NAME = "nuspec.xml";
    /**
     * Каталог с файлами пакета и хешем
     */
    private File packageFolder;

    /**
     * @param packageFolder папка с файлами пакета
     * @throws NugetFormatException некорректный формат папки
     */
    public MavenNupkg(File packageFolder) throws NugetFormatException {
        if (!packageFolder.isDirectory()) {
            throw new NugetFormatException(String.format("По указанному пути '%s' располагается не папка.", packageFolder.getAbsolutePath()));
        }
        if (!folderContainsPackageFile(packageFolder)) {
            throw new NugetFormatException(String.format("Каталог '%s' не содержит файла пакета.", packageFolder.getAbsolutePath()));
        }
        if (!folderContainsFile(packageFolder, HASH_FILE_NAME)) {
            throw new NugetFormatException(String.format("Каталог '%s' не содержит файла хеша.", packageFolder.getAbsolutePath()));
        }
        if (!folderContainsFile(packageFolder, NUSPEC_FILE_NAME)) {
            throw new NugetFormatException(String.format("Каталог '%s' не содержит файла спецификации.", packageFolder.getAbsolutePath()));
        }
        this.packageFolder = packageFolder;
        this.version = Version.parse(packageFolder.getName());
        this.id = MavenNupkg.this.getNuspecFile().getId();
        this.file = new File(packageFolder, MavenNupkg.this.getFileName());
    }

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        if (hash == null) {
            File hashFile = new File(packageFolder, HASH_FILE_NAME);
            hash = Hash.parse(hashFile);
        }
        return hash;
    }

    @Override
    public NuspecFile getNuspecFile() {
        if (nuspecFile == null) {
            File nuspec = new File(packageFolder, NUSPEC_FILE_NAME);
            try (InputStream inputStream = new FileInputStream(nuspec)) {
                nuspecFile = NuspecFile.Parse(inputStream);
            } catch (NugetFormatException | IOException e) {
                logger.error("При чтении nuspec-файла произошла ошибка.", e);
            }
        }
        return nuspecFile;
    }

    @Override
    public Long getSize() {
        return file.length();
    }

    @Override
    public InputStream getStream() throws IOException {
        return super.getStream();
    }

    /**
     * Проверка того, что каталог содержит файл пакета
     *
     * @param packageFolder каталог
     * @return true, если каталог содержит nupkg файл
     */
    private boolean folderContainsPackageFile(File packageFolder) {
        for (String fileName : packageFolder.list()) {
            if (fileName.toLowerCase().endsWith(Nupkg.DEFAULT_EXTENSION)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка того, что каталог содержит файл
     *
     * @param packageFolder каталог
     * @param fileName имя файла
     * @return true, если каталог содержит файл
     */
    private boolean folderContainsFile(File packageFolder, String fileName) {
        File testFile = new File(packageFolder, fileName);
        return testFile.exists();
    }
}

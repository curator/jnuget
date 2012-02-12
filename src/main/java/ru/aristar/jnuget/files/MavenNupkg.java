package ru.aristar.jnuget.files;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.imageio.IIOException;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.sources.NupkgFileExtensionFilter;

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
    public static final String NUSPEC_FILE = "nuspec.xml";
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Каталог с файлами пакета и хешем
     */
    private File packageFolder;

    /**
     * @param packageFolder папка с файлами пакета
     *
     * @throws NugetFormatException некорректный формат папки
     */
    public MavenNupkg(File packageFolder) throws NugetFormatException {
        if (!packageFolder.isDirectory()) {
            throw new NugetFormatException(String.format("По указанному пути '%s' располагается не папка.", packageFolder.getAbsolutePath()));
        }
        if (!folderContaintsPackageFile(packageFolder)) {
            throw new NugetFormatException(String.format("Каталог '%s' не содержит файла пакета.", packageFolder.getAbsolutePath()));
        }
        if (!folderContaintsHashFile(packageFolder)) {
            throw new NugetFormatException(String.format("Каталог '%s' не содержит файла хеша.", packageFolder.getAbsolutePath()));
        }
        String parsedId = packageFolder.getParentFile().getName();
        this.id = parsedId;
        this.packageFolder = packageFolder;
        this.version = Version.parse(packageFolder.getName());;
    }

    /**
     * Метод читает хеш из файла
     *
     * @param file файл с хешем
     * @return хеш пакета
     * @throws IOException ошибка чтения пакета
     */
    private Hash readHash(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            char[] buffer = new char[(int) file.length()];
            int charCount = fileReader.read(buffer);
            if (charCount == 0) {
                throw new IIOException("Прочитан пустой файл с контрольной суммой.");
            }
            return hash = Hash.parse(String.valueOf(buffer));
        }
    }

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        if (hash == null) {
            for (File curFile : packageFolder.listFiles()) {
                if (curFile.getName().equalsIgnoreCase(HASH_FILE_NAME)) {
                    hash = readHash(curFile);
                }
            }
        }
        return hash;
    }

    @Override
    public NuspecFile getNuspecFile() {
        if (nuspecFile == null) {
            for (File curFile : packageFolder.listFiles()) {
                if (curFile.getName().equalsIgnoreCase(NUSPEC_FILE)) {
                    try (InputStream input = new FileInputStream(curFile)) {
                        return nuspecFile = NuspecFile.Parse(input);
                    } catch (JAXBException | SAXException | IOException ex) {
                        logger.error("При чтении nuspec-файла произошла ошибка.", ex);
                    }
                }
            }
        }
        return nuspecFile;
    }

    @Override
    public Long getSize() {
        return getNupkgFile().length();
    }

    @Override
    public InputStream getStream() throws IOException {
        return super.getStream();
    }

    private File getNupkgFile() {
        if (file == null) {
            for (File curFile : packageFolder.listFiles(new NupkgFileExtensionFilter())) {
                if (curFile.getName().equalsIgnoreCase(getFileName())) {
                    file = curFile;
                }
            }
        }
        return file;
    }

    @Override
    public Date getUpdated() {
        if (updated == null) {
            updated = new Date(file.lastModified());
        }
        return updated;
    }

    /**
     * Проверка того, что каталог содержит файл пакета
     *
     * @param packageFolder каталог
     * @return true, если каталог содержит nupkg файл
     */
    private boolean folderContaintsPackageFile(File packageFolder) {
        for (String fileName : packageFolder.list()) {
            if (fileName.toLowerCase().endsWith(Nupkg.DEFAULT_EXTENSION)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка того, что каталог содержит файл хеша
     *
     * @param packageFolder каталог
     * @return true, если каталог содержит файл хеша
     */
    private boolean folderContaintsHashFile(File packageFolder) {
        for (String fileName : packageFolder.list()) {
            if (fileName.toLowerCase().endsWith(HASH_FILE_NAME)) {
                return true;
            }
        }
        return false;
    }
}

package ru.aristar.jnuget.files;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
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
    public static final String HASH_FILE = "hash.sha512";
    /**
     * Название извлеченного файла nuspec
     */
    public static final String NUSPEC_FILE = "nuspec.xml";

    protected MavenNupkg() {
        super();
    }

    public static MavenNupkg ParseDirectoryStructure(File packDir) throws NugetFormatException {
        MavenNupkg nupkg = new MavenNupkg();
        if (!packDir.isDirectory()) {
            throw new NugetFormatException(String.format("По указанному пути '%s' располагается не папка.", packDir.getPath()));
        }
        Version version = Version.parse(packDir.getName());
        String id = packDir.getParent();
        if (id == null) {
            throw new NugetFormatException("Id пакета не может быть пустым.");
        }

        nupkg.id = id;
        nupkg.file = packDir;
        nupkg.version = version;

        return nupkg;
    }
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private File nupkgFile;
    private Long size;

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        if (hash == null) {
            for (File curFile : file.listFiles()) {
                if (curFile.getName().equalsIgnoreCase(HASH_FILE)) {
                    try (FileReader input = new FileReader(curFile)) {
                        char[] buffer = new char[]{};
                        int charCount = input.read(buffer);
                        
                        if (charCount == 0) {
                            logger.error("Прочитан пустой файл с контрольной суммой.");
                            return null;
                        }
                        return hash = Hash.parse(String.valueOf(buffer));
                    } catch (IOException ex) {
                        logger.error("При чтении файла с контрольной суммой произошла ошибка.", ex);
                        throw ex;
                    }
                }
            }
        }
        return hash;
    }

    @Override
    public NuspecFile getNuspecFile() {
        if (nuspecFile == null) {
            for (File curFile : file.listFiles()) {
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
        if (size == null) {
            size = getNupkgFile().length();
        }
        return size;
    }

    @Override
    public InputStream getStream() throws IOException {
        File nupkgFile = getNupkgFile();
        if (nupkgFile != null) {
            return new FileInputStream(nupkgFile);
        }
        return null;
    }

    private File getNupkgFile() {
        if (nupkgFile != null) {
            return nupkgFile;
        }

        for (File curFile : file.listFiles(new NupkgFileExtensionFilter())) {
            if (curFile.getName().equalsIgnoreCase(getFileName())) {
                return nupkgFile = curFile;
            }
        }
        return null;
    }

    @Override
    public Date getUpdated() {
        if (updated == null) {
            updated = new Date(nupkgFile.lastModified());
        }
        return updated;
    }
}

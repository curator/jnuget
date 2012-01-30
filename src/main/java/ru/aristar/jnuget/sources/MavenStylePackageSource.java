package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 * Хранилище пакетов, имитирующее структуру хранилища Maven.
 *
 * @author unlocker
 */
public class MavenStylePackageSource implements PackageSource {
    /**
     * Название файла с контрольной суммой
     */
    public static final String HASH_FILE = "hash.sha512";
    /**
     * Название извлеченного файла nuspec
     */
    public static final String NUSPEC_FILE = "nuspec.xml";

    /**
     * Корневая папка, в которой расположены пакеты
     */
    private File rootFolder;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Стратегия помещения пакета в хранилище
     */
    private PushStrategy strategy;

    /**
     * Конструктор по умолчанию
     */
    public MavenStylePackageSource() {
    }

    /**
     * @param rootFolder папка с пакетами
     */
    public MavenStylePackageSource(File rootFolder) {
        this.rootFolder = rootFolder;
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
    }

    @Override
    public Collection<NupkgFile> getPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<NupkgFile> getLastVersionPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<NupkgFile> getPackages(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<NupkgFile> getPackages(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getLastVersionPackage(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getPackage(String id, Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getPackage(String id, Version version, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean pushPackage(NupkgFile nupkgFile, String apiKey) throws IOException {

        if (!getPushStrategy().canPush(nupkgFile, apiKey)) {
            return false;
        }
        File packageFolder = verifyPackageDestination(rootFolder, nupkgFile.getNuspecFile());

        // Открывает временный файл, копирует его в место постоянного хранения.
        File tmpDest = new File(packageFolder, nupkgFile.getFileName() + ".tmp");
        File finalDest = new File(packageFolder, nupkgFile.getFileName());
        try (ReadableByteChannel src = Channels.newChannel(nupkgFile.getStream());
                FileChannel dest = new FileOutputStream(tmpDest).getChannel()) {
            TempNupkgFile.fastChannelCopy(src, dest);
        }

        if (!tmpDest.renameTo(finalDest)) {
            throw new IOException("Не удалось переименовать файл " + tmpDest
                    + " в " + finalDest);
        }
        try {
            // Сохраняем nuspec
            File nuspecFile = new File(packageFolder, NUSPEC_FILE);
            try (FileOutputStream fileOutputStream = new FileOutputStream(nuspecFile)) {
                nupkgFile.getNuspecFile().saveTo(fileOutputStream);
            }

            // Сохраняем контрольную сумму
            File hashFile = new File(packageFolder, HASH_FILE);
            try (FileOutputStream output = new FileOutputStream(hashFile)) {
                nupkgFile.getHash().saveTo(output);
            }

        } catch (JAXBException | NoSuchAlgorithmException ex) {
            throw new IOException("Ошибка сохранения nuspec или хеш значения", ex);
        }
        return true;
    }

    /**
     * Проверяет наличие папки для хранения пакета, создает ее в случае необходимости
     *
     * @param rootFolder Корневая папка хранилища
     * @param source Файл спецификации
     * @return Папка назначения для пакета
     */
    private File verifyPackageDestination(File rootFolder, NuspecFile source) {
        String id = source.getId();
        Version version = source.getVersion();
        File packageFolder = new File(rootFolder, id.toLowerCase());
        if (!packageFolder.exists()) {
            packageFolder.mkdir();
        }
        File versionFolder = new File(packageFolder, version.toString());
        if (!versionFolder.exists()) {
            versionFolder.mkdir();
        }
        return versionFolder;
    }

    @Override
    public PushStrategy getPushStrategy() {
        if (strategy == null) {
            strategy = new SimplePushStrategy(false);
        }
        return strategy;
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        this.strategy = strategy;
    }
}

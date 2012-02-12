package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.*;

/**
 * Хранилище пакетов, имитирующее структуру хранилища Maven.
 *
 * @author unlocker
 */
public class MavenStylePackageSource implements PackageSource {

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
    public Collection<Nupkg> getPackages() {
        List<Nupkg> list = new ArrayList<>();
        for (String id : rootFolder.list()) {
            list.addAll(getPackagesById(id));
        }
        return list;
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        List<Nupkg> list = new ArrayList<>();
        for (String id : rootFolder.list()) {
            list.add(getLastVersionPackage(id));
        }
        return list;
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        return getPackages(id, true);
    }

    @Override
    public Collection<Nupkg> getPackages(String id, boolean ignoreCase) {
        return getPackagesById(id);
    }

    @Override
    public Nupkg getLastVersionPackage(String id) {
        return getLastVersionPackage(id, true);
    }

    @Override
    public Nupkg getLastVersionPackage(String id, boolean ignoreCase) {
        File idDir = new File(rootFolder, id);
        Nupkg lastVersion = null;
        for (File versionDir : idDir.listFiles()) {
            try {
                Nupkg temp = new MavenNupkg(versionDir);
                if (lastVersion == null || temp.getVersion().compareTo(lastVersion.getVersion()) > 0) {
                    lastVersion = temp;
                }
            } catch (NugetFormatException ex) {
                logger.error("Не удалось считать информацию о пакете.", ex);
            }
        }
        return lastVersion;
    }

    @Override
    public Nupkg getPackage(String id, Version version) {
        return getPackage(id, version, true);
    }

    @Override
    public Nupkg getPackage(String id, Version version, boolean ignoreCase) {
        File idDir = new File(rootFolder, id);
        for (File versionDir : idDir.listFiles()) {
            try {
                Nupkg temp = new MavenNupkg(versionDir);
                if (temp.getVersion() == version) {
                    return temp;
                }
            } catch (NugetFormatException ex) {
                logger.error("Не удалось считать информацию о пакете.", ex);
            }
        }
        return null;
    }

    @Override
    public boolean pushPackage(Nupkg nupkgFile, String apiKey) throws IOException {

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
            File nuspecFile = new File(packageFolder, MavenNupkg.NUSPEC_FILE);
            try (FileOutputStream fileOutputStream = new FileOutputStream(nuspecFile)) {
                nupkgFile.getNuspecFile().saveTo(fileOutputStream);
            }

            // Сохраняем контрольную сумму
            File hashFile = new File(packageFolder, MavenNupkg.HASH_FILE_NAME);
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

    private Collection<Nupkg> getPackagesById(String id) {
        //TODO Добавть тест, провести рефакторинг
        File idDir = new File(rootFolder, id);
        List<Nupkg> list = new ArrayList<>();
        for (File versionDir : idDir.listFiles()) {
            try {
                list.add(new MavenNupkg(versionDir));
            } catch (NugetFormatException ex) {
                logger.error("Не удалось считать информацию о пакете.", ex);
            }
        }
        return list;
    }
}

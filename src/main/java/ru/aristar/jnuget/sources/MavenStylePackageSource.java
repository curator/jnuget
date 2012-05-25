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
import java.util.Objects;
import javax.xml.bind.JAXBException;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.*;

/**
 * Хранилище пакетов, имитирующее структуру хранилища Maven.
 *
 * @author unlocker
 */
public class MavenStylePackageSource extends AbstractPackageSource<MavenNupkg> implements PackageSource<MavenNupkg> {

    /**
     * Корневая папка, в которой расположены пакеты
     */
    private File rootFolder;

    /**
     * Конструктор по умолчанию
     */
    public MavenStylePackageSource() {
    }

    /**
     * @return имя каталога, в котором находится хранилище пакетов
     */
    public String getRootFolderName() {
        return rootFolder == null ? null : rootFolder.getAbsolutePath();
    }

    /**
     * @param folderName имя каталога, в котором находится хранилище пакетов
     */
    public void setRootFolderName(String folderName) {
        rootFolder = new File(folderName);
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
    public Collection<MavenNupkg> getPackages() {
        List<MavenNupkg> list = new ArrayList<>();
        for (String id : rootFolder.list()) {
            list.addAll(getPackagesById(id));
        }
        return list;
    }

    @Override
    public Collection<MavenNupkg> getLastVersionPackages() {
        List<MavenNupkg> list = new ArrayList<>();
        for (String id : rootFolder.list()) {
            final MavenNupkg lastVersionPackage = getLastVersionPackage(id);
            if (lastVersionPackage != null) {
                list.add(lastVersionPackage);
            }
        }
        return list;
    }

    @Override
    public Collection<MavenNupkg> getPackages(String id) {
        return getPackagesById(id.toLowerCase());
    }

    @Override
    public MavenNupkg getLastVersionPackage(String id) {
        File idDir = new File(rootFolder, id);
        MavenNupkg lastVersion = null;
        for (File versionDir : idDir.listFiles()) {
            try {
                MavenNupkg temp = new MavenNupkg(versionDir);
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
    public MavenNupkg getPackage(String id, Version version) {
        File idDir = new File(rootFolder, id.toLowerCase());
        if (idDir.exists()) {
            for (File versionDir : idDir.listFiles()) {
                try {
                    MavenNupkg nupkg = new MavenNupkg(versionDir);
                    if (Objects.equals(nupkg.getVersion(), version)) {
                        return nupkg;
                    }
                } catch (NugetFormatException ex) {
                    logger.error("Не удалось считать информацию о пакете.", ex);
                }
            }
        }
        return null;
    }

    /**
     * Проверяет наличие папки для хранения пакета, создает ее в случае
     * необходимости
     *
     * @param rootFolder Корневая папка хранилища
     * @param source Файл спецификации
     * @return Папка назначения для пакета
     */
    private File verifyPackageDestination(File rootFolder, NuspecFile source) {
        String id = source.getId();
        Version version = source.getVersion();
        File packageFolder = new File(rootFolder, id.toLowerCase());
        File versionFolder = new File(packageFolder, version.toString());
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        return versionFolder;
    }

    /**
     * Возвращает коллекцию пакетов с указанным идентификатором
     *
     * @param id идентификатор пакета
     * @return коллекция пакетов
     */
    private Collection<MavenNupkg> getPackagesById(String id) {
        File idDir = new File(rootFolder, id);

        List<MavenNupkg> list = new ArrayList<>();
        if (idDir.exists()) {
            for (File versionDir : idDir.listFiles()) {
                try {
                    list.add(new MavenNupkg(versionDir));
                } catch (NugetFormatException ex) {
                    logger.error("Не удалось считать информацию о пакете.", ex);
                }
            }
        }
        return list;
    }

    @Override
    public void removePackage(Nupkg nupkg) {
        // Проверка наличия папки с пакетом
        File idDir = new File(rootFolder, nupkg.getId());
        File versionDir = new File(idDir, nupkg.getVersion().toString());
        if (!versionDir.exists()) {
            logger.info("Попытка удаления пакета, отсутствующего в хранилище "
                    + "(id: " + nupkg.getId() + ", version: " + nupkg.getVersion() + ")");
            return;
        }
        // Удаляем содержимое папки с версией
        for (File file : versionDir.listFiles()) {
            file.delete();
        }
        // Удаляем саму папку с версией
        versionDir.delete();
        //Если версий не осталось удаляем папку с идентификатором
        if (idDir.listFiles().length == 0) {
            idDir.delete();
        }
    }

    @Override
    public String toString() {
        return "MavenStylePackageSource{" + rootFolder + '}';
    }

    @Override
    public void refreshPackage(Nupkg nupkg) {
    }

    @Override
    protected void pushPackage(Nupkg nupkg) throws IOException {
        try {
            File packageFolder = verifyPackageDestination(rootFolder, nupkg.getNuspecFile());
            // Открывает временный файл, копирует его в место постоянного хранения.
            File tmpDest = new File(packageFolder, nupkg.getFileName() + ".tmp");
            File finalDest = new File(packageFolder, nupkg.getFileName());
            try (ReadableByteChannel src = Channels.newChannel(nupkg.getStream());
                    FileChannel dest = new FileOutputStream(tmpDest).getChannel()) {
                TempNupkgFile.fastChannelCopy(src, dest);
            }

            if (!tmpDest.renameTo(finalDest)) {
                throw new IOException("Не удалось переименовать файл " + tmpDest
                        + " в " + finalDest);
            }
            try {
                // Сохраняем nuspec
                File nuspecFile = new File(packageFolder, MavenNupkg.NUSPEC_FILE_NAME);
                try (FileOutputStream fileOutputStream = new FileOutputStream(nuspecFile)) {
                    nupkg.getNuspecFile().saveTo(fileOutputStream);
                }

                // Сохраняем контрольную сумму
                File hashFile = new File(packageFolder, MavenNupkg.HASH_FILE_NAME);
                nupkg.getHash().saveTo(hashFile);

            } catch (JAXBException | NoSuchAlgorithmException ex) {
                throw new IOException("Ошибка сохранения nuspec или хеш значения", ex);
            }
            logger.debug("Пакет {}:{} добавлен в хранилище",
                    new Object[]{nupkg.getNuspecFile().getId(), nupkg.getNuspecFile().getVersion()});
        } catch (NugetFormatException e) {
            throw new IOException("Некорректный формат спецификации файла", e);
        }
    }
}

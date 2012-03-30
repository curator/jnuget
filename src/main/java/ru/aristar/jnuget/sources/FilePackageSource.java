package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.*;
import ru.aristar.jnuget.sources.push.PushStrategy;
import ru.aristar.jnuget.sources.push.SimplePushStrategy;

/**
 * Хранилище пакетов, использующее стандартную для NuGet конфигурацию
 *
 * @author sviridov
 */
public class FilePackageSource implements PackageSource<ClassicNupkg> {

    /**
     * Папка с пакетами
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
     * Устанавливает корневую папку хранилища (если папка не существует -
     * производит попытку создать ее)
     *
     * @param rootFolder корневая пака хранилища
     */
    private void setRootFolder(File rootFolder) {
        final String folderName = rootFolder == null ? null : rootFolder.getAbsolutePath();
        logger.info("Устанавливается корневая папка хранилища: {}", new Object[]{folderName});
        this.rootFolder = rootFolder;
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

    }

    /**
     * Конструктор по умолчанию
     */
    public FilePackageSource() {
    }

    /**
     * @param rootFolder папка с пакетами
     */
    public FilePackageSource(File rootFolder) {
        setRootFolder(rootFolder);
    }

    /**
     * @return полное имя папки с пакетами
     */
    public String getFolderName() {
        return rootFolder == null ? null : rootFolder.getAbsolutePath();
    }

    /**
     * @param pathName полное имя папки с пакетами
     */
    public void setFolderName(String pathName) {
        setRootFolder(new File(pathName));
    }

    /**
     * Переименовывает файл с перезаписью существующего
     *
     * @param source источник
     * @param dest назначение
     * @return true, в случае успеха
     */
    protected boolean renameFile(File source, File dest) {
        if (dest.exists()) {
            dest.delete();
        }
        return source.renameTo(dest);
    }

    /**
     * Возвращает информацию по имеющимся пакетам
     *
     * @param filter фильтр файлов
     * @return Список объектов с информацией
     */
    private List<ClassicNupkg> getPackageList(FilenameFilter filter) {
        ArrayList<ClassicNupkg> packages = new ArrayList<>();
        for (File file : rootFolder.listFiles(filter)) {
            try {
                ClassicNupkg pack = new ClassicNupkg(file);
                packages.add(pack);
            } catch (NugetFormatException ex) {
                logger.warn("Не удалось разобрать имя файла", ex);
            }
        }
        return packages;
    }

    /**
     * Возвращает список пакетов, соответствующий выражению фильтра
     *
     * @param filter фильтр файлов
     * @return список пакетов
     */
    private Collection<ClassicNupkg> getPackages(FilenameFilter filter) {
        return getPackageList(filter);
    }

    @Override
    public Collection<ClassicNupkg> getPackages() {
        FilenameFilter filenameFilter = new NupkgFileExtensionFilter();
        return getPackages(filenameFilter);
    }

    @Override
    public ClassicNupkg getPackage(final String id, Version version) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, true);
        for (ClassicNupkg pack : getPackages(filenameFilter)) {
            if (pack.getId().equals(id) && pack.getVersion().equals(version)) {
                return pack;
            }
        }
        return null;
    }

    @Override
    public Collection<ClassicNupkg> getLastVersionPackages() {
        List<ClassicNupkg> allPackages = getPackageList(new NupkgFileExtensionFilter());
        return extractLastVersion(allPackages, true);
    }

    @Override
    public Collection<ClassicNupkg> getPackages(String id) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, false);
        return getPackages(filenameFilter);
    }

    @Override
    public ClassicNupkg getLastVersionPackage(String id) {
        return getLastVersionPackage(id, true);
    }

    @Override
    public Collection<ClassicNupkg> getPackages(String id, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        return getPackages(filenameFilter);
    }

    @Override
    public ClassicNupkg getLastVersionPackage(String id, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, ignoreCase);
        Collection<ClassicNupkg> lastVersion = extractLastVersion(getPackageList(filenameFilter), ignoreCase);
        if (lastVersion.isEmpty()) {
            return null;
        }
        return lastVersion.iterator().next();
    }

    @Override
    public ClassicNupkg getPackage(String id, Version version, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        for (ClassicNupkg nupkgFile : getPackages(filenameFilter)) {
            NuspecFile nuspec = nupkgFile.getNuspecFile();
            if (nuspec.getId().equals(id) && nuspec.getVersion().equals(version)) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public boolean pushPackage(Nupkg nupkgFile, String apiKey) throws IOException {
        if (!getPushStrategy().canPush(nupkgFile, apiKey)) {
            return false;
        }
        // Открывает временный файл, копирует его в место постоянного хранения.
        File tmpDest = new File(rootFolder, nupkgFile.getFileName() + ".tmp");
        File finalDest = new File(rootFolder, nupkgFile.getFileName());
        try (ReadableByteChannel src = Channels.newChannel(nupkgFile.getStream());
                FileChannel dest = new FileOutputStream(tmpDest).getChannel()) {
            TempNupkgFile.fastChannelCopy(src, dest);
        }
        if (!renameFile(tmpDest, finalDest)) {
            throw new IOException("Не удалось переименовать файл " + tmpDest
                    + " в " + finalDest);
        }
        return true;
    }

    /**
     * Извлекает информацию о последних версиях всех пакетов
     *
     * @param <K> Тип пакета NuGet
     * @param list общий список всех пакетов
     * @param ignoreCase нужно ли игнорировать регистр символов
     * @return список последних версий пакетов
     */
    public static <K extends Nupkg> Collection<K> extractLastVersion(
            Collection<K> list, boolean ignoreCase) {
        Map<String, K> map = new HashMap<>();
        for (K pack : list) {
            String packageId = ignoreCase ? pack.getId().toLowerCase() : pack.getId();
            // Указанный пакет еще учитывался
            if (!map.containsKey(packageId)) {
                map.put(packageId, pack);
            } else { // Пакет уже попадался, сравниваем версии
                Version saved = map.get(packageId).getVersion();
                // Версия пакета новее, чем сохраненная
                if (saved.compareTo(pack.getVersion()) < 0) {
                    map.put(packageId, pack);
                }
            }
        }
        return map.values();
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

    @Override
    public void removePackage(String id, Version version) {
        File pack = new File(rootFolder, id + "." + version.toString() + Nupkg.DEFAULT_EXTENSION);
        if (!pack.exists()) {
            logger.info("Попытка удаления пакета, отсутствующего в хранилище (id: " + id + ", version: " + version + ")");
            return;
        }
        pack.delete();
    }

    @Override
    public String toString() {
        return "FilePackageSource{" + rootFolder + '}';
    }

    @Override
    public void refreshPackage(Nupkg nupkg) {
    }
}

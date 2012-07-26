package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.ClassicNupkg;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.ui.descriptors.Property;

/**
 * Хранилище пакетов, использующее стандартную для NuGet конфигурацию
 *
 * @author sviridov
 */
public class ClassicPackageSource extends AbstractPackageSource<ClassicNupkg> implements PackageSource<ClassicNupkg> {

    /**
     * Папка с пакетами
     */
    private File rootFolder;

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
    public ClassicPackageSource() {
    }

    /**
     * @param rootFolder папка с пакетами
     */
    public ClassicPackageSource(File rootFolder) {
        setRootFolder(rootFolder);
    }

    /**
     * @return полное имя папки с пакетами
     */
    @Property()
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
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
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
        return extractLastVersion(allPackages);
    }

    @Override
    public Collection<ClassicNupkg> getPackages(String id) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        return getPackages(filenameFilter);
    }

    @Override
    public ClassicNupkg getLastVersionPackage(String id) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        Collection<ClassicNupkg> lastVersion = extractLastVersion(getPackageList(filenameFilter));
        if (lastVersion.isEmpty()) {
            return null;
        }
        return lastVersion.iterator().next();
    }

    @Override
    public void removePackage(Nupkg nupkg) {
        File pack = new File(rootFolder, nupkg.getFileName());
        if (!pack.exists()) {
            logger.info("Попытка удаления пакета, отсутствующего в хранилище (id: " + nupkg.getId() + ", version: " + nupkg.getVersion() + ")");
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

    @Override
    protected void processPushPackage(Nupkg nupkg) throws IOException {
        // Открывает временный файл, копирует его в место постоянного хранения.
        File tmpDest = new File(rootFolder, nupkg.getFileName() + ".tmp");
        File finalDest = new File(rootFolder, nupkg.getFileName());
        try (ReadableByteChannel src = Channels.newChannel(nupkg.getStream());
                FileChannel dest = new FileOutputStream(tmpDest).getChannel()) {
            TempNupkgFile.fastChannelCopy(src, dest);
        }
        if (!renameFile(tmpDest, finalDest)) {
            throw new IOException("Не удалось переименовать файл " + tmpDest
                    + " в " + finalDest);
        }
    }
}

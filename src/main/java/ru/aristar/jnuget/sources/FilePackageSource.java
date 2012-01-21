package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 * Хранилище пакетов, использующее стандартную для NuGet конфигурацию
 *
 * @author sviridov
 */
public class FilePackageSource implements PackageSource {

    /**
     * Папка с пакетами
     */
    private File rootFolder;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Конструктор по умолчанию
     */
    public FilePackageSource() {
    }

    /**
     * @param rootFolder папка с пакетами
     */
    public FilePackageSource(File rootFolder) {
        logger.info("Создано файловое хранилище с адресом: {}", new Object[]{rootFolder});
        this.rootFolder = rootFolder;
    }

    /**
     * Преобразует информацию в список файлов пакетов
     *
     * @param packages Информация о пакетах
     * @return Список файлов пакетов
     */
    private Collection<NupkgFile> convertIdsToPackages(Collection<NugetPackageId> packages) {
        ArrayList<NupkgFile> nupkgFiles = new ArrayList<>();
        for (NugetPackageId pack : packages) {
            NupkgFile current = convertIdToPackage(pack);
            if (current != null) {
                nupkgFiles.add(current);
            }
        }
        return nupkgFiles;
    }

    /**
     * Преобразует информацию в пакет
     *
     * @param pack информация о пакете
     * @return Файл пакета
     */
    private NupkgFile convertIdToPackage(NugetPackageId pack) {
        try {
            File file = new File(rootFolder, pack.toString());
            NupkgFile nupkgFile = new NupkgFile(file);
            return nupkgFile;
        } catch (IOException | JAXBException | SAXException e) {
            logger.warn("Не удалось прочитать пакет " + pack.toString(), e);
            return null;
        }
    }

    /**
     * Возвращает информацию по имеющимся пакетам
     *
     * @param filter фильтр файлов
     * @return Список объектов с информацией
     */
    private List<NugetPackageId> getPackageList(FilenameFilter filter) {
        ArrayList<NugetPackageId> packages = new ArrayList<>();
        for (File file : rootFolder.listFiles(filter)) {
            try {
                NugetPackageId info = NugetPackageId.parse(file.getName());
                packages.add(info);
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
    private Collection<NupkgFile> getPackages(FilenameFilter filter) {
        List<NugetPackageId> packages = getPackageList(filter);
        return convertIdsToPackages(packages);
    }

    @Override
    public Collection<NupkgFile> getPackages() {
        FilenameFilter filenameFilter = new NupkgFileExtensionFilter();
        return getPackages(filenameFilter);
    }

    @Override
    public NupkgFile getPackage(final String id, Version version) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, false);
        for (NupkgFile nupkgFile : getPackages(filenameFilter)) {
            NuspecFile nuspec = nupkgFile.getNuspecFile();
            if (nuspec.getId().equals(id) && nuspec.getVersion().equals(version)) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public Collection<NupkgFile> getLastVersionPackages() {
        List<NugetPackageId> allPackages = getPackageList(new NupkgFileExtensionFilter());
        Collection<NugetPackageId> lastVersions = extractLastVersion(allPackages, true);
        return convertIdsToPackages(lastVersions);
    }

    @Override
    public Collection<NupkgFile> getPackages(String id) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, false);
        return getPackages(filenameFilter);
    }

    @Override
    public NupkgFile getLastVersionPackage(String id) {
        return getLastVersionPackage(id, true);
    }

    @Override
    public Collection<NupkgFile> getPackages(String id, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        return getPackages(filenameFilter);
    }

    @Override
    public NupkgFile getLastVersionPackage(String id, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, ignoreCase);
        Collection<NugetPackageId> lastVersion = extractLastVersion(getPackageList(filenameFilter), ignoreCase);
        if (lastVersion.isEmpty()) {
            return null;
        }
        return convertIdToPackage(lastVersion.iterator().next());
    }

    @Override
    public NupkgFile getPackage(String id, Version version, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        for (NupkgFile nupkgFile : getPackages(filenameFilter)) {
            NuspecFile nuspec = nupkgFile.getNuspecFile();
            if (nuspec.getId().equals(id) && nuspec.getVersion().equals(version)) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public void pushPackage(NupkgFile nupkgFile) throws IOException {
        // Открывает временный файл, копирует его в место постоянного хранения.
        File tmpDest = new File(rootFolder, nupkgFile.getFileName() + ".tmp");
        File finalDest = new File(rootFolder, nupkgFile.getFileName());
        FileChannel dest;
        try (ReadableByteChannel src = Channels.newChannel(nupkgFile.getStream())) {
            dest = new FileOutputStream(tmpDest).getChannel();
            TempNupkgFile.fastChannelCopy(src, dest);
        }
        dest.close();
        if (!tmpDest.renameTo(finalDest)) {
            throw new IOException("Не удалось переименовать файл " + tmpDest
                    + " в " + finalDest);
        }
    }

    /**
     * Извлекает информацию о последних версиях всех пакетов
     *
     * @param list общий список всех пакетов
     * @param ignoreCase нужно ли игнорировать регистр символов
     * @return список последних версий пакетов
     */
    private Collection<NugetPackageId> extractLastVersion(
            Collection<NugetPackageId> list, boolean ignoreCase) {
        Map<String, NugetPackageId> map = new HashMap();

        for (NugetPackageId info : list) {
            String packageId = ignoreCase ? info.getId().toLowerCase() : info.getId();
            // Указанный пакет еще учитывался
            if (!map.containsKey(packageId)) {
                map.put(packageId, info);
            } else { // Пакет уже попадался, сравниваем версии
                Version saved = map.get(packageId).getVersion();
                // Версия пакета новее, чем сохраненная
                if (saved.compareTo(info.getVersion()) > 0) {
                    map.put(packageId, info);
                }
            }
        }
        return map.values();
    }
}

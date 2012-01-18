package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;

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
            try {
                File file = new File(rootFolder, pack.toString());
                NupkgFile nupkgFile = new NupkgFile(file);
                nupkgFiles.add(nupkgFile);
            } catch (IOException | JAXBException | SAXException e) {
                logger.warn("Не удалось прочитать пакет " + pack.toString(), e);
            }
        }
        return nupkgFiles;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<NupkgFile> getPackages(String id, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        return getPackages(filenameFilter);
    }

    @Override
    public NupkgFile getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public void pushPackage(NupkgFile nupkgFile) {
        /*
         * File file = new File("c:\\" + fileInfo.getFileName()); file.delete();
         * FileOutputStream outputStream = new FileOutputStream(file); byte[]
         * buffer = new byte[1024]; int len = 0; while ((len =
         * inputStream.read(buffer)) >= 0) { outputStream.write(buffer, 0, len);
         * } outputStream.flush(); outputStream.close();
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Collection<NugetPackageId> extractLastVersion(Collection<NugetPackageId> list, boolean ignoreCase) {
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

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
import ru.aristar.jnuget.files.*;

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
     * Стратегия помещения пакета в хранилище
     */
    private PushStrategy strategy;

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
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
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
     * Преобразует информацию в список файлов пакетов
     *
     * @param packages Информация о пакетах
     * @return Список файлов пакетов
     */
    private Collection<Nupkg> convertIdsToPackages(Collection<NugetPackageId> packages) {
        ArrayList<Nupkg> nupkgFiles = new ArrayList<>();
        for (NugetPackageId pack : packages) {
            try {
                Nupkg current = convertIdToPackage(pack);
                nupkgFiles.add(current);
            } catch (JAXBException | IOException | SAXException | NugetFormatException e) {
                logger.warn("Не удалось прочитать пакет " + pack.toString(), e);
            }
        }
        return nupkgFiles;
    }

    /**
     * Преобразует информацию в пакет
     *
     * @param pack информация о пакете
     * @return Файл пакета
     * @throws JAXBException ошибка разбора XML
     * @throws IOException ошибка чтения файла с диска
     * @throws SAXException ошибка изменения пространства имен
     * @throws NugetFormatException ошибка формата пакета 
     */
    private ClassicNupkg convertIdToPackage(NugetPackageId pack)
            throws JAXBException, IOException, SAXException, NugetFormatException {
        File file = new File(rootFolder, pack.toString());
        ClassicNupkg nupkgFile = new ClassicNupkg(file);
        return nupkgFile;
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
    private Collection<Nupkg> getPackages(FilenameFilter filter) {
        List<NugetPackageId> packages = getPackageList(filter);
        return convertIdsToPackages(packages);
    }

    @Override
    public Collection<Nupkg> getPackages() {
        FilenameFilter filenameFilter = new NupkgFileExtensionFilter();
        return getPackages(filenameFilter);
    }

    @Override
    public Nupkg getPackage(final String id, Version version) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, true);
        for (Nupkg nupkgFile : getPackages(filenameFilter)) {
            NuspecFile nuspec = nupkgFile.getNuspecFile();
            if (nuspec.getId().equals(id) && nuspec.getVersion().equals(version)) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        List<NugetPackageId> allPackages = getPackageList(new NupkgFileExtensionFilter());
        Collection<NugetPackageId> lastVersions = extractLastVersion(allPackages, true);
        return convertIdsToPackages(lastVersions);
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, false);
        return getPackages(filenameFilter);
    }

    @Override
    public Nupkg getLastVersionPackage(String id) {
        return getLastVersionPackage(id, true);
    }

    @Override
    public Collection<Nupkg> getPackages(String id, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        return getPackages(filenameFilter);
    }

    @Override
    public Nupkg getLastVersionPackage(String id, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id, ignoreCase);
        Collection<NugetPackageId> lastVersion = extractLastVersion(getPackageList(filenameFilter), ignoreCase);
        if (lastVersion.isEmpty()) {
            return null;
        }
        NugetPackageId packageId = lastVersion.iterator().next();
        try {
            return convertIdToPackage(packageId);
        } catch (JAXBException | IOException | SAXException | NugetFormatException e) {
            logger.warn("Ошибка чтения архивного файла пакета " + packageId, e);
            return null;
        }
    }

    @Override
    public Nupkg getPackage(String id, Version version, boolean ignoreCase) {
        FilenameFilter filenameFilter = new SingleIdPackageFileFilter(id);
        for (Nupkg nupkgFile : getPackages(filenameFilter)) {
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
        FileChannel dest;
        try (ReadableByteChannel src = Channels.newChannel(nupkgFile.getStream())) {
            dest = new FileOutputStream(tmpDest).getChannel();
            TempNupkgFile.fastChannelCopy(src, dest);
        }
        dest.close();
        if (!renameFile(tmpDest, finalDest)) {
            throw new IOException("Не удалось переименовать файл " + tmpDest
                    + " в " + finalDest);
        }
        return true;
    }

    /**
     * Извлекает информацию о последних версиях всех пакетов
     *
     * @param list общий список всех пакетов
     * @param ignoreCase нужно ли игнорировать регистр символов
     * @return список последних версий пакетов
     */
    protected Collection<NugetPackageId> extractLastVersion(
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
                if (saved.compareTo(info.getVersion()) < 0) {
                    map.put(packageId, info);
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
}

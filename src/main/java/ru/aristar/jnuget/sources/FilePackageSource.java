package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NupkgFile;

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
     * Фильтр, оставляющий только корректный файлы пакетов
     *
     * @return true, если файл является файлом пакета, иначе false
     */
    private FilenameFilter getNupkgFileNameFilter() {
        return new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return NupkgFile.isValidFileName(name);

            }
        };
    }

    @Override
    public Collection<NupkgFile> getPackages() {
        ArrayList<NupkgFile> nupkgFiles = new ArrayList<>();
        FilenameFilter filenameFilter = getNupkgFileNameFilter();
        for (File file : rootFolder.listFiles(filenameFilter)) {
            try {
                NupkgFile nupkgFile = new NupkgFile(file);
                nupkgFiles.add(nupkgFile);
            } catch (IOException | JAXBException e) {
                logger.warn("Не удалось прочитать пакет " + file.getName(), e);
            }
        }
        return nupkgFiles;
    }
}

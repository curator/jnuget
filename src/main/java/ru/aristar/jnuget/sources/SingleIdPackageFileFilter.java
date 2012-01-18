package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Фильтр файлов пакетов по заданному идентификатору
 * @author Unlocker
 */
public class SingleIdPackageFileFilter extends NupkgFileExtensionFilter implements FilenameFilter {

    /**
     * Идентификатор пакета
     */
    private final String id;
    
    /**
     * Игнорировать ли регистр символов
     */
    private final boolean ignoreCase;

    /**
     * Фильтр файлов пакетов по заданному идентификатору
     * @param id Идентификатор пакета
     * @param ignoreCase Игнорировать ли регистр символов
     */
    public SingleIdPackageFileFilter(String id, boolean ignoreCase) {
        this.id = ignoreCase ? id.toLowerCase() : id;
        this.ignoreCase = ignoreCase;
    }

    /**
     * Фильтр файлов пакетов по заданному идентификатору, 
     * игнорирующий регистр символов
     * @param id Идентификатор пакета
     */
    public SingleIdPackageFileFilter(String id) {
        this(id, true);
    }

    @Override
    public boolean accept(File dir, String name) {
        final String path = ignoreCase ? name.toLowerCase() : name;
        return super.accept(dir, name) && path.startsWith(id);
    }
}

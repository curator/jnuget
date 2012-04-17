package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Фильтр файлов пакетов по заданному идентификатору
 *
 * @author Unlocker
 */
public class SingleIdPackageFileFilter extends NupkgFileExtensionFilter implements FilenameFilter {

    /**
     * Идентификатор пакета
     */
    private final String id;

    /**
     * Фильтр файлов пакетов по заданному идентификатору, игнорирующий регистр
     * символов
     *
     * @param id Идентификатор пакета
     */
    public SingleIdPackageFileFilter(String id) {
        this.id = id.toLowerCase();
    }

    @Override
    public boolean accept(File dir, String name) {
        final String path = name.toLowerCase();
        return super.accept(dir, name) && path.startsWith(id);
    }
}

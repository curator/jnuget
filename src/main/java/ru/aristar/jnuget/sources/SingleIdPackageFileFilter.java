package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Unlocker
 */
public class SingleIdPackageFileFilter extends NupkgFileExtensionFilter implements FilenameFilter {

    /**
     *
     */
    private final String id;
    /**
     *
     */
    private final boolean ignoreCase;

    /**
     *
     * @param id
     * @param ignoreCase
     */
    public SingleIdPackageFileFilter(String id, boolean ignoreCase) {
        this.id = ignoreCase ? id.toLowerCase() : id;
        this.ignoreCase = ignoreCase;
    }

    /**
     *
     * @param id
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

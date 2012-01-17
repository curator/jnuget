package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FilenameFilter;
import ru.aristar.jnuget.files.NupkgFile;

/**
 *
 * @author Unlocker
 */
public class NupkgFileExtensionFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return NupkgFile.isValidFileName(name);
    }
}

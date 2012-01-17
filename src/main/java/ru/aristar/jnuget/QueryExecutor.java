package ru.aristar.jnuget;

import java.util.Collection;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.sources.FilePackageSource;

/**
 *
 * @author sviridov
 */
class QueryExecutor {

    public Collection<NupkgFile> execQuery(FilePackageSource packageSource, final String filter) {
        return packageSource.getPackages();
    }
}

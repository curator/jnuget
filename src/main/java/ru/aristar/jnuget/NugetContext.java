package ru.aristar.jnuget;

import java.net.URI;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.rss.PackageEntry;

/**
 *
 * @author sviridov
 */
public class NugetContext {

    private final URI rootUri;

    public NugetContext(URI rootUri) {
        this.rootUri = rootUri;
    }

    public URI getRootUri() {
        return rootUri;
    }

    public PackageEntry createPackageEntry(NupkgFile nupkgFile) {
        PackageEntry entry = new PackageEntry(nupkgFile, this);
        return entry;
    }
}

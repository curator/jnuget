package ru.aristar.jnuget;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
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

    public PackageEntry createPackageEntry(NupkgFile nupkgFile) throws NoSuchAlgorithmException, IOException {
        PackageEntry entry = new PackageEntry(nupkgFile, this);
        return entry;
    }

    public NuPkgToRssTransformer createToRssTransformer() {
        return new NuPkgToRssTransformer(this);
    }
}

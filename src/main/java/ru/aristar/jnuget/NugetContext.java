package ru.aristar.jnuget;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.rss.PackageEntry;

/**
 *
 * @author sviridov
 */
public class NugetContext {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final URI rootUri;

    public NugetContext(URI rootUri) {
        this.rootUri = rootUri;
        logger.debug("Создан контекст для приложения {}", new Object[]{rootUri});
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

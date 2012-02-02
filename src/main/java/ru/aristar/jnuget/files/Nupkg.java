package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import ru.aristar.jnuget.Version;

/**
 *
 * @author Unlocker
 */
public interface Nupkg {
    /**
     * Расширение по умолчанию
     */
    String DEFAULT_EXTENSION = ".nupkg";

    String getFileName();

    Hash getHash() throws NoSuchAlgorithmException, IOException;

    NuspecFile getNuspecFile();

    Long getSize();

    InputStream getStream() throws IOException;

    Date getUpdated();

    String getId();

    Version getVersion();
    
}

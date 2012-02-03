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
public class MavenNupkg implements Nupkg {

    @Override
    public String getFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NuspecFile getNuspecFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InputStream getStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Date getUpdated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Version getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

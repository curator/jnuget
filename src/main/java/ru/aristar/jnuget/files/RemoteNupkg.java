package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.rss.PackageEntry;

/**
 * Пакет в удаленном хранилище
 *
 * @author sviridov
 */
public class RemoteNupkg implements Nupkg {

    /**
     * Спецификация пакета
     */
    private final NuspecFile nuspec;
    /**
     * HASH пакета
     */
    private final Hash hash;
    /**
     * Размер пакет а в байтах
     */
    private final long size;
    /**
     * Дата обновления пакета
     */
    private final Date updated;

    /**
     * @param entry RSS сообщение с данными пакета
     * @throws NugetFormatException ошибка в формате RSS сообщения
     */
    public RemoteNupkg(PackageEntry entry) throws NugetFormatException {
        this.nuspec = new NuspecFile(entry);
        this.hash = Hash.parse(entry.getProperties().getPackageHash());
        this.size = entry.getProperties().getPackageSize();
        this.updated = entry.getUpdated();
    }

    @Override
    public String getFileName() {
        return getId() + "." + getVersion().toString() + DEFAULT_EXTENSION;
    }

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        return hash;
    }

    @Override
    public NuspecFile getNuspecFile() {
        return nuspec;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public InputStream getStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public String getId() {
        return nuspec.getId();
    }

    @Override
    public Version getVersion() {
        return nuspec.getVersion();
    }

    @Override
    public void load() throws IOException {
    }
}

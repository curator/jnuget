package ru.aristar.jnuget.files;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
     * URL, по которому можно получить поток с пакетом
     */
    private URI sourceUri;

    /**
     * @param entry RSS сообщение с данными пакета
     * @throws NugetFormatException ошибка в формате RSS сообщения
     */
    public RemoteNupkg(PackageEntry entry) throws NugetFormatException {
        try {
            this.nuspec = new NuspecFile(entry);
            this.hash = Hash.parse(entry.getProperties().getPackageHash());
            this.sourceUri = new URI(entry.getContent().getSrc());
            this.size = entry.getProperties().getPackageSize();
            this.updated = entry.getUpdated();
        } catch (URISyntaxException e) {
            throw new NugetFormatException("Некорректный формат URI пакета", e);
        }
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
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource webResource = client.resource(sourceUri);
            return webResource.get(InputStream.class);
        } catch (Exception e) {
            throw new IOException("Ошибка получения потока пакета из удаленного ресурса", e);
        }
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

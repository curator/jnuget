package ru.aristar.jnuget.files;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.EnumSet;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final URI sourceUri;
    /**
     * Логгер
     */
    private transient Logger logger;

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

    /**
     * @param nuspec спецификация пакета
     * @param hash HASH пакета
     * @param size размер пакета
     * @param updated дата обновления пакета
     * @param sourceUri URI источника
     */
    public RemoteNupkg(NuspecFile nuspec, Hash hash, long size, Date updated, URI sourceUri) {
        this.nuspec = nuspec;
        this.hash = hash;
        this.size = size;
        this.updated = updated;
        this.sourceUri = sourceUri;
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
        return getStream(sourceUri);
    }

    /**
     * Получает поток с данными пакета из удаленного URI
     *
     * @param uri адрес потока
     * @return поток с данными пакета
     * @throws IOException ошибка чтения данных
     */
    private InputStream getStream(URI uri) throws IOException {
        getLogger().debug("Загрузка пакета из {}", new Object[]{uri});
        try {
            DefaultClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            Client client = Client.create(config);
            WebResource webResource = client.resource(uri);
            ClientResponse response = webResource.get(ClientResponse.class);
            switch (response.getClientResponseStatus()) {
                case ACCEPTED:
                case OK: {
                    return response.getEntity(InputStream.class);
                }
                case FOUND:
                case MOVED_PERMANENTLY: {
                    getLogger().debug("Получено перенаправление");
                    String redirectUriString = response.getHeaders().get("Location").get(0);
                    URI redirectUri = new URI(redirectUriString);
                    return getStream(redirectUri);
                }
                default:
                    throw new IOException("Статус сообщения " + response.getClientResponseStatus() + " не поддерживается");
            }
        } catch (UniformInterfaceException | ClientHandlerException | URISyntaxException e) {
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

    @Override
    public int hashCode() {
        int intHash = 7;
        try {
            intHash = 61 * intHash + Objects.hashCode(this.getHash());
        } catch (NoSuchAlgorithmException | IOException e) {
            intHash = 61 * intHash + Objects.hashCode(this.getId()) + Objects.hashCode(this.getVersion());
        }
        return intHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RemoteNupkg other = (RemoteNupkg) obj;
        try {
            return Objects.equals(this.getHash(), other.getHash());
        } catch (NoSuchAlgorithmException | IOException e) {
            return Objects.equals(this.getId(), other.getId()) && Objects.equals(this.getVersion(), other.getVersion());
        }
    }

    /**
     * @return the logger
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

    @Override
    public EnumSet<Frameworks> getTargetFramework() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

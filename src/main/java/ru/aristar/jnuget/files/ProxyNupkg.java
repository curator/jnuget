package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Прокси представление пакета из удаленного хранилища
 *
 * @author sviridov
 */
public class ProxyNupkg implements Nupkg {

    /**
     * Локальное хранилище пакетов
     */
    private final PackageSource localPackageSource;
    /**
     * Пакет из локального хранилища
     */
    private Nupkg localNupkg;
    /**
     * Пакет из удаленного хранилища
     */
    private final RemoteNupkg remoteNupkg;

    /**
     * @param localPackageSource локальное хранилище пакетов
     * @param remoteNupkg пакет из удаленного хранилища
     */
    public ProxyNupkg(PackageSource localPackageSource, RemoteNupkg remoteNupkg) {
        this.localPackageSource = localPackageSource;
        this.remoteNupkg = remoteNupkg;
    }

    @Override
    public String getFileName() {
        return remoteNupkg.getFileName();
    }

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        return remoteNupkg.getHash();
    }

    @Override
    public NuspecFile getNuspecFile() {
        return remoteNupkg.getNuspecFile();
    }

    @Override
    public Long getSize() {
        return remoteNupkg.getSize();
    }

    @Override
    public InputStream getStream() throws IOException {
        if (localNupkg != null) {
            return localNupkg.getStream();
        }
        localPackageSource.pushPackage(remoteNupkg, null);
        localNupkg = localPackageSource.getPackage(remoteNupkg.getId(), remoteNupkg.getVersion());
        return localNupkg.getStream();
    }

    @Override
    public Date getUpdated() {
        return remoteNupkg.getUpdated();
    }

    @Override
    public String getId() {
        return remoteNupkg.getId();
    }

    @Override
    public Version getVersion() {
        return remoteNupkg.getVersion();
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
}

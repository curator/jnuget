package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.EnumSet;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.nuspec.NuspecFile;
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
    private transient PackageSource localPackageSource;
    /**
     * Пакет из локального хранилища
     */
    private Nupkg localNupkg;
    /**
     * Пакет из удаленного хранилища
     */
    private final RemoteNupkg remoteNupkg;
    /**
     * Логгер
     */
    private transient Logger logger;

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
            getLogger().debug("Пакет выгружается {}:{} из локального хранилища",
                    new Object[]{getId(), getVersion()});
            return localNupkg.getStream();
        }
        getLogger().debug("Получение данных для пакета {}:{} в удаленном репозитории",
                new Object[]{getId(), getVersion()});
        localPackageSource.pushPackage(remoteNupkg);
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

    /**
     * @return the logger
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

    /**
     * @param localPackageSource хранилище пакетов
     */
    public void setPackageSource(PackageSource localPackageSource) {
        this.localPackageSource = localPackageSource;
    }

    @Override
    public EnumSet<Framework> getTargetFramework() {
        return remoteNupkg.getTargetFramework();
    }
}

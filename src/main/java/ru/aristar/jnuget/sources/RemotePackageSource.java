package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class RemotePackageSource implements PackageSource<Nupkg> {

    /**
     * Удаленное хранилище пакетов
     */
    private NugetClient remoteStorage = new NugetClient();
    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Стратегия публикации пакетов
     */
    protected PushStrategy pushStrategy = new SimplePushStrategy(false);

    /**
     * @param url URL удаленного хранилища
     */
    public void setUrl(String url) {
        remoteStorage.setUrl(url);
    }

    /**
     * @return URL удаленного хранилища
     */
    public String getUrl() {
        return remoteStorage.getUrl();
    }

    @Override
    public Nupkg getLastVersionPackage(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TempNupkgFile getPackage(String id, Version version) {
        return getPackage(id, version, true);
    }

    @Override
    public TempNupkgFile getPackage(String id, Version version, boolean ignoreCase) {
        try {
            return remoteStorage.getPackage(id, version);
        } catch (Exception e) {
            logger.warn("Ошибка получения пакета из удаленного хранилища", e);
            return null;
        }
    }

    @Override
    public Collection<Nupkg> getPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getPackages(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PushStrategy getPushStrategy() {
        return pushStrategy;
    }

    @Override
    public boolean pushPackage(Nupkg file, String apiKey) throws IOException {
        try {
            remoteStorage.putPackage(file, apiKey);
            return true;
        } catch (Exception e) {
            logger.warn("Ошибка помещения пакета в удаленное хранилище", e);
            return false;
        }
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        this.pushStrategy = strategy;
    }

    @Override
    public void removePackage(String id, Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

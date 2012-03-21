package ru.aristar.jnuget.sources;

import com.sun.jersey.api.client.UniformInterfaceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.RemoteNupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.rss.PackageFeed;

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
     * Получает список пакетов из удаленного хранилища
     *
     * @param filter фильтр отбора пактов
     * @return список пакетов
     */
    private Collection<Nupkg> getPackagesFromRemoteStorage(String filter) {
        try {
            PackageFeed feed;
            ArrayList<Nupkg> result = new ArrayList<>();
            int groupCount = 200;
            int skip = 0;
            do {
                feed = remoteStorage.getPackages(filter, null, groupCount, null, skip);
                for (PackageEntry entry : feed.getEntries()) {
                    try {
                        RemoteNupkg remoteNupkg = new RemoteNupkg(entry);
                        result.add(remoteNupkg);
                    } catch (NugetFormatException e) {
                        logger.warn("Ошибка обработки пакета из удаленного хранилища", e);
                    }
                }
                skip = skip + groupCount;
            } while (feed != null && !feed.getEntries().isEmpty());
            logger.debug("Завершено получение пакетов. Статус: skip={}; feed.entries={}",
                    new Object[]{skip, feed == null ? null : feed.getEntries().size()});
            return result;
        } catch (NugetFormatException | UniformInterfaceException e) {
            logger.warn("Ошибка получения пакета из удаленного хранилища", e);
            return new ArrayList<>();
        }
    }

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
        return getLastVersionPackage(id, true);
    }

    @Override
    public Nupkg getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        return getPackagesFromRemoteStorage("IsLatestVersion");
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
        return getPackagesFromRemoteStorage(null);
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        return getPackages(id, true);
    }

    @Override
    public Collection<Nupkg> getPackages(String id, boolean ignoreCase) {
        String filter = "tolower(Id) eq '" + id + "'";
        return getPackagesFromRemoteStorage(filter);
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
        } catch (UniformInterfaceException e) {
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

    @Override
    public String toString() {
        return "RemotePackageSource{" + remoteStorage.getUrl() + '}';
    }
}

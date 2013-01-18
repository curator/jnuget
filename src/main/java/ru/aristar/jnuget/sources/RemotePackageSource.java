package ru.aristar.jnuget.sources;

import com.sun.jersey.api.client.UniformInterfaceException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.ClientFactory;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.RemoteNupkg;
import static ru.aristar.jnuget.sources.AbstractPackageSource.extractLastVersion;
import ru.aristar.jnuget.sources.push.AfterTrigger;
import ru.aristar.jnuget.sources.push.BeforeTrigger;
import ru.aristar.jnuget.sources.push.ModifyStrategy;
import ru.aristar.jnuget.sources.push.NugetPushException;

/**
 *
 * @author sviridov
 */
public class RemotePackageSource implements PackageSource<RemoteNupkg> {

    /**
     * Удаленное хранилище пакетов
     */
    protected NugetClient remoteStorage = new NugetClient();
    /**
     * Пул запросов в удаленное хранилище
     */
    private static ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);
    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Стратегия публикации пакетов
     */
    protected ModifyStrategy pushStrategy = new ModifyStrategy(false);
    /**
     * Имя хранилища
     */
    private String name;

    /**
     * Получает список пакетов из удаленного хранилища
     *
     * @param filter фильтр отбора пактов
     * @return список пакетов
     */
    private Collection<RemoteNupkg> getPackagesFromRemoteStorage(String filter) {
        try {
            List<RemoteNupkg> result = new ArrayList<>();
            result = Collections.synchronizedList(result);
            int count = remoteStorage.getPackageCount(false);
            int groupCount = 200;
            logger.debug("Получение {} пакетов из удаленного сервера группами по {}",
                    new Object[]{count, groupCount});
            ClientFactory clientFactory = new ClientFactory(getUrl());
            forkJoinPool.invoke(new GetRemotePackageFeedAction(groupCount, result, 0, count, clientFactory));
            logger.debug("Завершено получение пакетов count={}", new Object[]{result.size()});
            return result;
        } catch (IOException | URISyntaxException e) {
            logger.warn("Ошибка получения пакета из удаленного хранилища", e);
            return new ArrayList<>(1);
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

    /**
     * @param apiKey ключ доступа к удаленному хранилищу
     */
    public void setApiKey(String apiKey) {
        remoteStorage.setApiKey(apiKey);
    }

    /**
     * @return ключ доступа к удаленному хранилищу
     */
    public String getApiKey() {
        return remoteStorage.getApiKey();
    }

    @Override
    public RemoteNupkg getLastVersionPackage(String id) {
        String filter = "tolower(Id) eq '" + id + "'";
        Collection<RemoteNupkg> nupkgs = getPackagesFromRemoteStorage(filter);
        Collection<RemoteNupkg> result = extractLastVersion(nupkgs);
        if (!result.isEmpty()) {
            return result.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public Collection<RemoteNupkg> getLastVersionPackages() {
        return getPackagesFromRemoteStorage("IsLatestVersion");
    }

    @Override
    public RemoteNupkg getPackage(String id, Version version) {
        Collection<RemoteNupkg> nupkgs = getPackages(id);
        for (RemoteNupkg remoteNupkg : nupkgs) {
            if (version.equals(remoteNupkg.getVersion())) {
                return remoteNupkg;
            }
        }
        return null;
    }

    @Override
    public Collection<RemoteNupkg> getPackages() {
        return getPackagesFromRemoteStorage(null);
    }

    @Override
    public Collection<RemoteNupkg> getPackages(String id) {
        String filter = "tolower(Id) eq '" + id + "'";
        return getPackagesFromRemoteStorage(filter);
    }

    @Override
    public ModifyStrategy getPushStrategy() {
        return pushStrategy;
    }

    @Override
    public boolean pushPackage(Nupkg nupkg) throws IOException {
        try {
            //TODO перенести метод в стратегию
            for (BeforeTrigger pushTrigger : getPushStrategy().getBeforePushTriggers()) {
                pushTrigger.doAction(nupkg, this);
            }
            remoteStorage.putPackage(nupkg);
            for (AfterTrigger pushTrigger : getPushStrategy().getAftherPushTriggers()) {
                pushTrigger.doAction(nupkg, this);
            }
            return true;
        } catch (UniformInterfaceException | NugetPushException e) {
            logger.warn("Ошибка помещения пакета в удаленное хранилище", e);
            return false;
        }
    }

    @Override
    public void setPushStrategy(ModifyStrategy strategy) {
        this.pushStrategy = strategy;
    }

    @Override
    public void removePackage(Nupkg nupkg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "RemotePackageSource{" + remoteStorage.getUrl() + '}';
    }

    @Override
    public void refreshPackage(Nupkg nupkg) {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String storageName) {
        this.name = storageName;
    }
}

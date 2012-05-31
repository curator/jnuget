package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.RemoteNupkg;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.rss.PackageFeed;

/**
 * Задача получения списка пактов из удаленного хранилища
 *
 * @author sviridov
 */
public class GetRemotePackageFeedAction extends RecursiveAction {

    private static final int PACKAGES_PER_THREAD = 2000;
    private final int packageFeedSize;
    private final List<RemoteNupkg> packages;
    private final int low;
    private final int top;
    private final NugetClient client;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(GetRemotePackageFeedAction.class);

    public GetRemotePackageFeedAction(int packageFeedSize, List<RemoteNupkg> packages, final int low, final int top, NugetClient client) {
        logger.debug("Создание потока для диапазона: {}:{}", new Object[]{low, top});
        this.packageFeedSize = packageFeedSize;
        this.packages = packages;
        this.low = low;
        this.top = top;
        this.client = client;
    }

    @Override
    protected void compute() {
        logger.debug("Обработка для верхняя граница = {}; Нижняя граница = {}", new Object[]{top, low});
        if (top - low <= PACKAGES_PER_THREAD) {
            loadPackages();
        } else {
            final int middle = (top + low) / 2;
            logger.debug("Верхняя граница = {}; Нижняя граница = {}; Середина = {};", new Object[]{top, low, middle});
            GetRemotePackageFeedAction bottomAction = new GetRemotePackageFeedAction(packageFeedSize, packages, low, middle, client);
            GetRemotePackageFeedAction topAction = new GetRemotePackageFeedAction(packageFeedSize, packages, middle, top, client);
            invokeAll(bottomAction, topAction);
        }
    }

    private void loadPackages() {
        logger.debug("Получение пакетов для диапазона: {}:{}", new Object[]{low, top});
        ArrayList<RemoteNupkg> result = new ArrayList<>();
        try {
            for (int skip = low; skip < top; skip = skip + packageFeedSize) {
                int cnt = top - skip;
                if (cnt > packageFeedSize) {
                    cnt = packageFeedSize;
                }
                logger.debug("Запрос пакетов с {} по {}", new Object[]{skip, skip + cnt});
                PackageFeed feed = client.getPackages(null, null, cnt, null, skip);
                logger.debug("Получено {} пакетов", new Object[]{feed.getEntries().size()});
                for (PackageEntry entry : feed.getEntries()) {
                    try {
                        logger.debug("Добавление пакета {}:{}", new Object[]{entry.getTitle(), entry.getProperties().getVersion()});
                        RemoteNupkg remoteNupkg = new RemoteNupkg(entry);
                        result.add(remoteNupkg);
                    } catch (NugetFormatException e) {
                        logger.warn("Ошибка обработки пакета из удаленного хранилища", e);
                    }
                }
                logger.debug("Обработано {} пакетов", new Object[]{feed.getEntries().size()});
            }
            logger.debug("Получено {} пакетов для диапазона: {}:{}", new Object[]{result.size(), low, top});
            packages.addAll(result);
        } catch (IOException | URISyntaxException e) {
            logger.warn("Ошибка получения пакетов из удаленного хранилища", e);
        }
    }
}

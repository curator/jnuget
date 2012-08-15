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

    /**
     * Количество потоков, начиная с которого идет последовательная обработка
     */
    private static final int PACKAGES_PER_THREAD = 2000;
    /**
     * Размер запроса в хранилище
     */
    private final int packageFeedSize;
    /**
     * Список, в который складываются полученные пакеты
     */
    private final List<RemoteNupkg> packages;
    /**
     * Нижняя граница списка пакетов
     */
    private final int low;
    /**
     * Верхняя граница списка пакетов
     */
    private final int top;
    /**
     * Клиент удаленного хранилища
     */
    private final NugetClient client;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(GetRemotePackageFeedAction.class);

    /**
     * @param packageFeedSize размер запроса в хранилище
     * @param packages список, в который складываются полученные пакеты
     * @param low нижняя граница списка пакетов
     * @param top верхняя граница списка пакетов
     * @param client клиент удаленного хранилища
     */
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
        if (top == low) {
            return;
        }
        logger.debug("Обработка пакетов {}-{}", new Object[]{low, top});
        if (top - low <= PACKAGES_PER_THREAD) {
            loadPackages();
        } else {
            final int middle = (top + low) / 2;
            logger.trace("Верхняя граница = {}; Нижняя граница = {}; Середина = {};", new Object[]{top, low, middle});
            GetRemotePackageFeedAction bottomAction = new GetRemotePackageFeedAction(packageFeedSize, packages, low, middle, client);
            GetRemotePackageFeedAction topAction = new GetRemotePackageFeedAction(packageFeedSize, packages, middle, top, client);
            invokeAll(bottomAction, topAction);
        }
    }

    /**
     * Последовательное получение пакетов из хранилища
     */
    protected void loadPackages() {
        logger.trace("Получение пакетов для диапазона: {}:{}", new Object[]{low, top});
        ArrayList<RemoteNupkg> result = new ArrayList<>();
        try {
            int skip = low;
            int packageSize = packageFeedSize;
            do {
                int cnt = top - skip;
                if (cnt > packageSize) {
                    cnt = packageSize;
                }
                logger.trace("Запрос пакетов с {} по {}", new Object[]{skip, skip + cnt});
                PackageFeed feed = client.getPackages(null, null, cnt, null, skip);
                if (feed != null) {
                    logger.trace("Получено {} пакетов для {}-{}", new Object[]{feed.getEntries().size(), skip, skip + cnt});
                    for (PackageEntry entry : feed.getEntries()) {
                        try {
                            logger.trace("Добавление пакета {}:{}", new Object[]{entry.getTitle(), entry.getProperties().getVersion()});
                            RemoteNupkg remoteNupkg = new RemoteNupkg(entry);
                            result.add(remoteNupkg);
                        } catch (NugetFormatException e) {
                            logger.warn("Ошибка обработки пакета {} : {}  из удаленного хранилища. Причина: {}",
                                    new Object[]{entry.getTitle(), entry.getProperties().getVersion(), e.getMessage()});
                        }
                    }
                    logger.trace("Обработано {} пакетов", new Object[]{feed.getEntries().size()});
                    packageSize = feed.getEntries().size();
                } else {
                    logger.warn("Не удалось получить пакеты для {}-{} c {} попыток", new Object[]{skip, skip + cnt, client.MAX_TRY_COUNT});
                }
                skip = skip + packageSize;
            } while (skip < top && packageSize > 0);
            logger.trace("Получено {} пакетов для диапазона: {}:{}", new Object[]{result.size(), low, top});
            packages.addAll(result);
        } catch (IOException | URISyntaxException e) {
            logger.warn("Ошибка получения пакетов из удаленного хранилища", e);
        }
    }
}

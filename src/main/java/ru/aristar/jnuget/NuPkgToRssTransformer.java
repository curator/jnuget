package ru.aristar.jnuget;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.ClassicNupkg;
import ru.aristar.jnuget.rss.EntryProperties;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.rss.PackageFeed;
import ru.aristar.jnuget.rss.PackageIdAndVersionComparator;

/**
 *
 * @author sviridov
 */
public class NuPkgToRssTransformer {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Контекст сервера
     */
    private final NugetContext context;

    /**
     *
     * @param context контекст сервера
     */
    public NuPkgToRssTransformer(NugetContext context) {
        this.context = context;
    }

    /**
     * Преобразует коллекцию ClassicNupkg в RSS структуру
     *
     * @param files колллекция исходных файлов
     * @param orderBy поле, по которому производится упорядочивание
     * @param skip пропустить первые
     * @param top всего вывести
     * @return RSS структура
     */
    public PackageFeed transform(Collection<ClassicNupkg> files, String orderBy, int skip, int top) {
        //TODO filter=IsLatestVersion, orderBy=DownloadCount desc,Id, skip=0, top=30, searchTerm='', targetFramework='net40'
        PackageFeed feed = new PackageFeed();
        feed.setId(context.getRootUri().toString());
        feed.setUpdated(new Date());
        feed.setTitle("Packages");
        List<PackageEntry> packageEntrys = new ArrayList<>();
        
        for (ClassicNupkg nupkg : files) {
            try {
                PackageEntry entry = context.createPackageEntry(nupkg);
                entry.getProperties().setIsLatestVersion(Boolean.FALSE);
                addServerInformationInToEntry(entry);
                packageEntrys.add(entry);
            } catch (IOException | NoSuchAlgorithmException e) {
                logger.warn("Ошибка сбора информации о пакете", e);
            }
        }
        Collections.sort(packageEntrys, new PackageIdAndVersionComparator());
        markLastVersion(packageEntrys);
        logger.debug("Получено {} записей о пакетах", new Object[]{packageEntrys.size()});
        packageEntrys = cutPackageList(skip, top, packageEntrys);
        logger.debug("Подготовлено {} записей о пакетах", new Object[]{packageEntrys.size()});
        feed.setEntries(packageEntrys);
        return feed;
    }

    /**
     * Безопасно уменьшает в размерах список пакетов
     *
     * @param skip количество пакетов, которые следует пропустить с начала
     * списка
     * @param top количество пакетов, не более которого будет возвращено в
     * обрезаном списке
     * @param packageEntrys исходный список
     * @return обрезанный список
     */
    protected <T> List<T> cutPackageList(final int skip, final int top, List<T> packageEntrys) {
        if (packageEntrys == null || packageEntrys.isEmpty()) {
            return packageEntrys;
        }
        try {
            int newSkip = normalizeSkip(skip, packageEntrys.size());
            int newTop = normalizeTop(skip, packageEntrys.size(), top);
            return packageEntrys.subList(skip, newSkip + newTop);
        } catch (Exception e) {
            logger.error("Ошибка получения подсписка пакетов: "
                    + "skip={} top={} size={}", new Object[]{skip, top, packageEntrys.size()});
            throw e;
        }
    }
    
    private void addServerInformationInToEntry(PackageEntry entry) {
        EntryProperties properties = entry.getProperties();
        //TODO Не факт, что сюда
        //****************************
        properties.setIconUrl("");
        properties.setLicenseUrl("");
        properties.setProjectUrl("");
        properties.setReportAbuseUrl("");
        //***************************
        properties.setDownloadCount(-1);
        properties.setVersionDownloadCount(-1);
        properties.setRatingsCount(0);
        properties.setVersionRatingsCount(0);
        properties.setRating(Double.valueOf(0));
        properties.setVersionRating(Double.valueOf(0));
    }

    /**
     * Помечает последние версии пакетов в списке. Список должен быть
     * отсортирован по возрастанию идентификатора пакета и его версии
     *
     * @param packageEntrys список информации о пакетах
     */
    protected void markLastVersion(List<PackageEntry> packageEntrys) {
        if (packageEntrys == null || packageEntrys.isEmpty()) {
            return;
        }
        packageEntrys.get(packageEntrys.size() - 1).getProperties().setIsLatestVersion(true);
        PackageEntry prev = null;
        for (int i = packageEntrys.size() - 2; i >= 0; i--) {
            PackageEntry current = packageEntrys.get(i);
            if (prev != null) {
                String prevId = prev.getTitle();
                String currId = current.getTitle();
                if (!currId.equals(prevId)) {
                    current.getProperties().setIsLatestVersion(true);
                } else {
                    current.getProperties().setIsLatestVersion(false);
                }
            }
            prev = current;
        }
    }

    /**
     * Нормализует значение запроса количества пропускаемых пакетов
     *
     * @param skip исходное число пропускаемых пакетов
     * @param size общее количество пакетов
     * @return нормализованное количество пакетов
     */
    private int normalizeSkip(int skip, int size) {
        if (skip < 0) {
            skip = 0;
        }
        if (skip > size - 1) {
            skip = size - 1;
        }
        return skip;
    }

    /**
     * Нормализует значение запроса количества возвращаемых пакетов
     *
     * @param skip число пропускаемых пакетов
     * @param size общее количество пакетов
     * @param top исходное количество возвращаемых пакетов
     * @return нормализованное количество пакетов
     */
    private int normalizeTop(int skip, int size, int top) {
        if (top < 0) {
            top = size;
        }
        if (top + skip > size) {
            top = size - skip;
        }
        return top;
    }
}

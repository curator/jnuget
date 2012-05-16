package ru.aristar.jnuget.rss;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.NugetContext;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public abstract class NuPkgToRssTransformer {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @return Контекст сервера
     */
    protected abstract NugetContext getContext();

    /**
     * Конструктор по умолчанию
     */
    public NuPkgToRssTransformer() {
    }

    /**
     * Преобразует коллекцию Nupkg в RSS структуру
     *
     * @param files колллекция исходных файлов
     * @param orderBy поле, по которому производится упорядочивание
     * @param skip пропустить первые
     * @param top всего вывести
     * @return RSS структура
     */
    public PackageFeed transform(Collection<? extends Nupkg> files, String orderBy, int skip, int top) {
        PackageFeed feed = new PackageFeed();
        feed.setId(getContext().getRootUri().toString());
        feed.setUpdated(new Date());
        feed.setTitle("Packages");
        List<PackageEntry> packageEntrys = new ArrayList<>();

        for (Nupkg nupkg : files) {
            try {
                PackageEntry entry = getContext().createPackageEntry(nupkg);
                entry.getProperties().setIsLatestVersion(Boolean.FALSE);
                addServerInformationInToEntry(entry);
                packageEntrys.add(entry);
            } catch (Exception e) {
                logger.warn("Ошибка сбора информации о пакете " + nupkg, e);
            }
        }
        Collections.sort(packageEntrys, getPackageComparator(orderBy));
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
     * @param <T> тип объектов, для которых производится обрезание списка
     * @param skip количество пакетов, которые следует пропустить с начала
     * списка
     * @param top количество пакетов, не более которого будет возвращено в
     * обрезаном списке
     * @param objects исходный список
     * @return обрезанный список
     */
    protected <T> List<T> cutPackageList(final int skip, final int top, List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return objects;
        }
        try {
            int newSkip = normalizeSkip(skip, objects.size());
            int newTop = normalizeTop(skip, objects.size(), top);
            return objects.subList(skip, newSkip + newTop);
        } catch (Exception e) {
            logger.error("Ошибка получения подсписка пакетов: "
                    + "skip={} top={} size={}", new Object[]{skip, top, objects.size()});
            throw e;
        }
    }

    /**
     * Добавляет информацию, доступную только серверу в пакет (рейтинг и число
     * скачиваний)
     *
     * @param entry
     */
    private void addServerInformationInToEntry(PackageEntry entry) {
        EntryProperties properties = entry.getProperties();
        //TODO Разобраться что это за URL
        properties.setReportAbuseUrl("");
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

    /**
     * Возвращает компаратор пакетов на основе строкового представления условия
     * поиска
     *
     * @param orderByClause строковое представление условия поиска
     * @return копаратор пакетов
     */
    protected Comparator<PackageEntry> getPackageComparator(final String orderByClause) {
        final String normalOrderBy = orderByClause == null ? "" : orderByClause.toLowerCase();
        switch (normalOrderBy) {
            case "updated":
                return new PackageUpdateDateDescComparator();
            case "downloadcount":
                return new PackageDownloadCountComparator();
            default:
                return new PackageIdAndVersionComparator();
        }

    }
}

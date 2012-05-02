package ru.aristar.jnuget.rss;

import java.util.Comparator;
import java.util.Date;

/**
 * Компаратор, сравнивающий пакеты по дате создания
 *
 * @author sviridov
 */
public class PackageUpdateDateDescComparator implements Comparator<PackageEntry> {

    @Override
    public int compare(PackageEntry o1, PackageEntry o2) {
        Date date1 = o1.getUpdated();
        Date date2 = o2.getUpdated();
        if (date1 == null && date2 == null) {
            return 0;
        }
        if (date1 == null) {
            return 1;
        }
        if (date2 == null) {
            return -1;
        }
        return -date1.compareTo(date2);
    }
}

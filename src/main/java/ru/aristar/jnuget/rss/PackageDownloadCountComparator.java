package ru.aristar.jnuget.rss;

import java.util.Comparator;

/**
 * Сравнивает пакеты по количеству скачиваний
 *
 * @author sviridov
 */
public class PackageDownloadCountComparator implements Comparator<PackageEntry> {

    @Override
    public int compare(PackageEntry o1, PackageEntry o2) {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        }
        Integer downloadCount1 = o1.getProperties().getDownloadCount();
        Integer downloadCount2 = o2.getProperties().getDownloadCount();
        if (downloadCount1 == null && downloadCount2 == null) {
            return 0;
        } else if (downloadCount1 == null) {
            return -1;
        } else if (downloadCount2 == null) {
            return 1;
        } else {
            return downloadCount1.compareTo(downloadCount2);
        }
    }
}

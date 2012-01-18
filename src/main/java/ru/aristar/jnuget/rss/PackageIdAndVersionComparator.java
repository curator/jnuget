package ru.aristar.jnuget.rss;

import java.util.Comparator;
import ru.aristar.jnuget.Version;

/**
 * Компаратор, производящий сравнение сначало по идентификатору пакета, а потом
 * по его версии
 *
 * @author sviridov
 */
public class PackageIdAndVersionComparator extends PackageIdComparator implements Comparator<PackageEntry> {

    @Override
    public int compare(PackageEntry o1, PackageEntry o2) {
        int idCompare = super.compare(o1, o2);
        if (idCompare != 0) {
            return idCompare;
        }
        Version v1 = o1.getProperties().getVersion();
        Version v2 = o2.getProperties().getVersion();
        if (v1 == null && v2 == null) {
            return 0;
        }

        if (v1 == null) {
            return -1;
        }

        if (v2 == null) {
            return 1;
        }

        return v1.compareTo(v2);
    }
}

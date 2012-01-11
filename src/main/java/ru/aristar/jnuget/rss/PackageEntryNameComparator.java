package ru.aristar.jnuget.rss;

import java.util.Comparator;

/**
 *
 * @author sviridov
 */
public class PackageEntryNameComparator implements Comparator<PackageEntry> {

    @Override
    public int compare(PackageEntry o1, PackageEntry o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }

        if (o1 == null) {
            return -1;
        }

        if (o2 == null) {
            return 1;
        }

        return o1.getTitle().compareTo(o2.getTitle());
    }
}

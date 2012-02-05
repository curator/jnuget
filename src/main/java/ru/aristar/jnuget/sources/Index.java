package ru.aristar.jnuget.sources;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class Index {

    private TreeMap<String, TreeMap<Version, Nupkg>> treeMap = new TreeMap<>();

    public class LastVersionIterator implements Iterator<Nupkg> {

        private final Iterator<TreeMap<Version, Nupkg>> iterator;

        public LastVersionIterator(Iterator<TreeMap<Version, Nupkg>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Nupkg next() {
            return iterator.next().lastEntry().getValue();
        }

        @Override
        public void remove() {
        }
    }

    public class AllPackagesIterator implements Iterator<Nupkg> {

        private final Iterator<TreeMap<Version, Nupkg>> iterator;
        private Iterator<Nupkg> currentGroup;

        public AllPackagesIterator(Iterator<TreeMap<Version, Nupkg>> iterator) {
            this.iterator = iterator;
            if (iterator.hasNext()) {
                currentGroup = iterator.next().values().iterator();
            } else {
                currentGroup = Collections.EMPTY_LIST.iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return currentGroup.hasNext();
        }

        @Override
        public Nupkg next() {
            return currentGroup.next();
        }

        @Override
        public void remove() {
        }
    }

    public Collection<Nupkg> getPackageById(String id) {
        return treeMap.get(id).values();
    }

    public Iterator<Nupkg> getLastVersions() {
        return new LastVersionIterator(treeMap.values().iterator());
    }

    public Iterator<Nupkg> getAllPackages() {
        return new AllPackagesIterator(treeMap.values().iterator());
    }

    public void put(Nupkg nupkg) {
        TreeMap<Version, Nupkg> packageGroup = treeMap.get(nupkg.getId());
        if (packageGroup == null) {
            packageGroup = new TreeMap<>();
            treeMap.put(nupkg.getId(), packageGroup);
        }
        packageGroup.put(nupkg.getVersion(), nupkg);
    }

    public void putAll(Iterable<Nupkg> nupkgs) {
        for (Nupkg nupkg : nupkgs) {
            put(nupkg);
        }
    }

    public void putAll(Nupkg... nupkgs) {
        for (Nupkg nupkg : nupkgs) {
            put(nupkg);
        }
    }

    public void remove(Nupkg nupkg) {
        TreeMap<Version, Nupkg> packageGroup = treeMap.get(nupkg.getId());
        if (packageGroup != null) {
            packageGroup.remove(nupkg.getVersion());
        }
    }
}

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

    /**
     * Итератор, перебирающий все пакеты в индексе
     */
    public class AllPackagesIterator implements Iterator<Nupkg> {

        /**
         * Итератор по всем группам пакетов (пакеты группированы по
         * идентификаторам)
         */
        private final Iterator<TreeMap<Version, Nupkg>> iterator;
        /**
         * Текущая группа пакетов (разные версии одного пакета)
         */
        private Iterator<Nupkg> currentGroup;

        /**
         * @param iterator итератор индекса
         */
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
            Nupkg result = currentGroup.next();
            if (!currentGroup.hasNext()) {
                switchGroup();
            }
            return result;
        }

        @Override
        public void remove() {
        }

        /**
         * Переключает текущую группу пакетов на следующую
         */
        private void switchGroup() {
            if (iterator.hasNext()) {
                currentGroup = iterator.next().values().iterator();
            }
        }
    }

    public Collection<Nupkg> getPackageById(String id) {
        return treeMap.get(id).values();
    }

    public Iterator<Nupkg> getLastVersions() {
        return new LastVersionIterator(treeMap.values().iterator());
    }

    /**
     * Возвращает итератор, перебирающий все пакеты в индексе
     *
     * @return итератор пакетов
     */
    public Iterator<Nupkg> getAllPackages() {
        return new AllPackagesIterator(treeMap.values().iterator());
    }

    /**
     * Помещает пакет в индекс
     *
     * @param nupkg пакет, который следует поместить в индекс
     */
    public void put(Nupkg nupkg) {
        TreeMap<Version, Nupkg> packageGroup = treeMap.get(nupkg.getId());
        if (packageGroup == null) {
            packageGroup = new TreeMap<>();
            treeMap.put(nupkg.getId(), packageGroup);
        }
        packageGroup.put(nupkg.getVersion(), nupkg);
    }

    /**
     * Помещает все пакеты в индекс
     *
     * @param nupkgs набор пакетов
     */
    public void putAll(Iterable<Nupkg> nupkgs) {
        for (Nupkg nupkg : nupkgs) {
            put(nupkg);
        }
    }

    /**
     * Помещает все пакеты в индекс
     *
     * @param nupkgs набор пакетов
     */
    public void putAll(Nupkg... nupkgs) {
        for (Nupkg nupkg : nupkgs) {
            put(nupkg);
        }
    }

    /**
     * Удаляет пакет из индекса
     *
     * @param nupkg пакет, который необходимо удалить из индекса
     */
    public void remove(Nupkg nupkg) {
        TreeMap<Version, Nupkg> packageGroup = treeMap.get(nupkg.getId());
        if (packageGroup != null) {
            packageGroup.remove(nupkg.getVersion());
        }
    }
}

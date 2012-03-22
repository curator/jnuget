package ru.aristar.jnuget.sources;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
 * Индекс хранилища пакетов
 *
 * @author sviridov
 */
public class Index {

    /**
     * Индекс пакетов
     */
    private SortedMap<String, SortedMap<Version, Nupkg>> treeMap = Collections.synchronizedSortedMap(new TreeMap<String, SortedMap<Version, Nupkg>>());

    /**
     * Итератор, перебирающий последние версии всех пакетов
     */
    public class LastVersionIterator implements Iterator<Nupkg> {

        /**
         * Итератор групп пакетов (по идентификаторам)
         */
        private final Iterator<SortedMap<Version, Nupkg>> iterator;

        /**
         * @param iterator Итератор групп пакетов (по идентификаторам)
         */
        public LastVersionIterator(Iterator<SortedMap<Version, Nupkg>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Nupkg next() {
            SortedMap<Version, Nupkg> packageGroup = iterator.next();
            Version key = packageGroup.lastKey();
            final Nupkg nupkg = packageGroup.get(key);
            return nupkg;
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
        private final Iterator<SortedMap<Version, Nupkg>> iterator;
        /**
         * Текущая группа пакетов (разные версии одного пакета)
         */
        private Iterator<Nupkg> currentGroup;

        /**
         * @param iterator итератор индекса
         */
        public AllPackagesIterator(Iterator<SortedMap<Version, Nupkg>> iterator) {
            this.iterator = iterator;
            if (iterator.hasNext()) {
                currentGroup = iterator.next().values().iterator();
            } else {
                List<Nupkg> emptyList = Collections.emptyList();
                currentGroup = emptyList.iterator();
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

    /**
     * Возвращает все версии указанного пакета
     *
     * @param id идентификатор пакета
     * @return все версии пакета из индекса
     */
    public Collection<Nupkg> getPackageById(String id) {
        id = id.toLowerCase();
        SortedMap<Version, Nupkg> group = treeMap.get(id);
        if (group == null) {
            return Arrays.asList(new Nupkg[]{});
        } else {
            return treeMap.get(id).values();
        }
    }

    /**
     * @return последние версии пакетов
     */
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
        SortedMap<Version, Nupkg> packageGroup = treeMap.get(nupkg.getId().toLowerCase());
        if (packageGroup == null) {
            packageGroup = Collections.synchronizedSortedMap(new TreeMap<Version, Nupkg>());
            treeMap.put(nupkg.getId().toLowerCase(), packageGroup);
        }
        packageGroup.put(nupkg.getVersion(), nupkg);
    }

    /**
     * Помещает все пакеты в индекс
     *
     * @param nupkgs набор пакетов
     */
    public void putAll(Iterable<? extends Nupkg> nupkgs) {
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
        SortedMap<Version, Nupkg> packageGroup = treeMap.get(nupkg.getId());
        if (packageGroup != null) {
            packageGroup.remove(nupkg.getVersion());
        }
    }

    /**
     * Возвращает последнюю версию пакета из индекса
     *
     * @param id идентификатор пакета
     * @return последняя версия пакета
     */
    public Nupkg getLastVersion(String id) {
        id = id.toLowerCase();
        SortedMap<Version, Nupkg> group = treeMap.get(id);
        if (group != null) {
            Version key = group.lastKey();
            return group.get(key);
        } else {
            return null;
        }
    }

    /**
     * Возвращает пакет по спецификации
     *
     * @param id идеентификатор пакета
     * @param version версия пакета
     * @return пакет
     */
    public Nupkg getPackage(String id, Version version) {
        id = id.toLowerCase();
        SortedMap<Version, Nupkg> group = treeMap.get(id);
        if (group != null) {
            return group.get(version);
        } else {
            return null;
        }
    }

    /**
     * Размер индекса
     *
     * @return количество пакетов в индексе
     */
    public int size() {
        int result = 0;
        for (SortedMap<Version, Nupkg> a : treeMap.values()) {
            result = result + a.size();
        }
        return result;
    }

    /**
     * Сохранить индекс в поток
     *
     * @param outputStream поток для сохранения
     */
    public void saveTo(OutputStream outputStream) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    /**
     * Загрузить ранее сохраненный индекс из потока
     *
     * @param inputStream поток для чтения индекса
     * @return загруженый объект индекса
     */
    public static Index loadFrom(InputStream inputStream) {
        throw new UnsupportedOperationException("Метод не реализован");
    }
}

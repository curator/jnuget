package ru.aristar.jnuget.ui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Контроллер содержимого хранилища
 *
 * @author sviridov
 */
@ManagedBean(name = "storageContents")
@RequestScoped
public class StorageContentsController {

    /**
     * Символы, с которых могут начинаться иммена пакетов
     */
    private static final char[] PACKAGE_ID_START_LETTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'J', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    /**
     * Идентификатор пакета
     */
    private String packageId;
    /**
     * Идентификатор хранилища
     */
    private int storageId;
    /**
     * Количество пакетов, которое необходимо пропусть
     */
    private int low = 0;
    /**
     * Количество отображаемых пакетов
     */
    private int displayCount = 200;
    /**
     * Список пакетов в хранилище
     */
    protected DataModel<Nupkg> packages;
    /**
     * Хранилище
     */
    private PackageSource<Nupkg> storage;

    /**
     * @return идентификатор хранилища
     */
    public int getStorageId() {
        return storageId;
    }

    /**
     * @param storageId идентификатор хранилища
     */
    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    /**
     * @return количество пакетов в хранилище
     */
    public int getPackageCount() {
        return packages.getRowCount();
    }

    /**
     * @return идентификатор пакета
     */
    public String getPackageId() {
        return packageId;
    }

    /**
     * @param packageId идентификатор пакета
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public PackageSource<Nupkg> getStorage() {
        return storage;
    }

    public void setStorage(PackageSource<Nupkg> storage) {
        this.storage = storage;
    }

    /**
     * Инициализация хранилища
     */
    public void init() {
        if (storageId == -1) {
            storage = PackageSourceFactory.getInstance().getPackageSource();
        } else {
            storage = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        }
        if (packageId == null) {
            ArrayList<Nupkg> nupkgs = new ArrayList<>(storage.getLastVersionPackages());
            packages = new ListDataModel<>(nupkgs);
        } else {
            ArrayList<Nupkg> nupkgs = new ArrayList<>(storage.getPackages(packageId));
            packages = new ListDataModel<>(nupkgs);
        }
    }

    /**
     * @return список пар: первый символ в идентификаторе пакета, позиция
     * первого подходящегопакета в списке пакетов
     */
    public List<Map.Entry<Character, Integer>> getLettersRefs() {
        Map<Character, Integer> result = new HashMap<>(PACKAGE_ID_START_LETTERS.length);
        for (int i = 0; i < PACKAGE_ID_START_LETTERS.length; i++) {
            final int firstId = getFirstId(PACKAGE_ID_START_LETTERS[i]);
            if (firstId != -1) {
                result.put(PACKAGE_ID_START_LETTERS[i], firstId);
            }
        }
        List<Map.Entry<Character, Integer>> resultList = new ArrayList<>(result.entrySet());
        Collections.sort(resultList, new EntryKeyComparator());
        return resultList;
    }

    /**
     * @return список пакетов в хранилище
     */
    public DataModel<Nupkg> getPackages() {
        return packages;
    }

    /**
     * @return нижняя граница списка пакетов
     */
    public int getLow() {
        return low;
    }

    /**
     * @param low нижняя граница списка отображаемых пакетов
     */
    public void setLow(int low) {
        this.low = low;
    }

    /**
     * @return количество отображаемых одновременно пакетов
     */
    public int getDisplayCount() {
        return displayCount;
    }

    /**
     * @return верхняя граница списка отображаемых пакетов
     */
    public int getTop() {
        int result = getDisplayCount() + getLow();
        if (result > getPackageCount()) {
            result = getPackageCount();
        }
        return result;
    }

    /**
     * @param count количество отображаемых одновременно пакетов
     */
    public void setDisplayCount(int count) {
        this.displayCount = count;
    }

    public int normalizeTop(int top) {
        return top > getPackageCount() ? getPackageCount() : top;
    }

    /**
     * @return список идентификаторов переходов
     */
    public List<Integer> getSkipList() {
        //Расчет числа отображаемых слева и справа от текущего диапазонов
        int topPageCount = (int) ceil(((float) getPackageCount() - (float) getTop()) / (float) getDisplayCount());
        int lowPageCount = (int) ceil(((float) getLow()) / (float) getDisplayCount());
        topPageCount = min(topPageCount, 6 - min(lowPageCount, 3));
        lowPageCount = min(lowPageCount, 6 - topPageCount);

        final int listSize = lowPageCount + topPageCount + 5;
        Integer[] arrayList = new Integer[listSize];
        //Первые два элемента массива (первая страница и предыдущая)
        arrayList[0] = 0;
        arrayList[1] = max(getLow() - getDisplayCount(), 0);

        for (int i = 0; i < lowPageCount; i++) {
            arrayList[2 + i] = getLow() - (lowPageCount - i) * getDisplayCount();
        }
        //Текущая страница
        arrayList[2 + lowPageCount] = getLow();

        for (int i = 0; i < topPageCount; i++) {
            arrayList[3 + lowPageCount + i] = getTop() + i * getDisplayCount();
        }

        int lastPageCount = getPackageCount() % getDisplayCount();
        if (lastPageCount == 0) {
            lastPageCount = getDisplayCount();
        }

        //последние два элемента массива (последняя страница и следующая)
        arrayList[listSize - 2] = min(getLow() + getDisplayCount(), max(0, getPackageCount() - lastPageCount));
        arrayList[listSize - 1] = max((getPackageCount() - lastPageCount), 0);
        return Arrays.asList(arrayList);
    }

    /**
     * Возвращает идентификатор первого элемента, идентификатор которого
     * начинается с указанного символа
     *
     * @param symbol первый символ идентификатора
     * @return идентификатор элемента
     */
    private int getFirstId(final char symbol) {
        Predicate<Nupkg> predicate = new FirstIdPredicate(symbol);
        return Iterables.indexOf(packages, predicate);




    }

    /**
     * Предикат поиска пакета, идентификатор которого начинается с указанного
     * символа
     */
    private class FirstIdPredicate implements Predicate<Nupkg> {

        /**
         * Первый символ идентификатора пакета в нижнем регистре
         */
        private final char lowerSymbol;

        /**
         * @param symbol Первый символ идентификатора пакета
         */
        public FirstIdPredicate(char symbol) {
            this.lowerSymbol = Character.toLowerCase(symbol);
        }

        /**
         * @param input пакет для проверки
         * @return true, если первый символ совпадает с запрошенным
         */
        @Override
        public boolean apply(Nupkg input) {
            return Character.toLowerCase(input.getId().charAt(0)) == lowerSymbol;
        }
    }

    /**
     * Компаратор пар ключ/значение по ключам
     */
    private static class EntryKeyComparator implements Comparator<Entry<Character, Integer>> {

        @Override
        public int compare(Entry<Character, Integer> o1, Entry<Character, Integer> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    }
}

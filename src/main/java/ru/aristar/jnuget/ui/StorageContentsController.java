package ru.aristar.jnuget.ui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private DataModel<Nupkg> packages;
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
     * Инициализация хранилища
     */
    public void init() {
        storage = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        ArrayList<Nupkg> nupkgs = new ArrayList<>(storage.getPackages());
        packages = new ListDataModel<>(nupkgs);
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

    /**
     * @return список идентификаторов переходов
     */
    public List<Integer> getSkipList() {
        ArrayList<Integer> arrayList = new ArrayList<>(4);
        arrayList.add(0, 0);
        arrayList.add(1, getLow() - getDisplayCount());
        arrayList.add(2, getLow() + getDisplayCount());
        arrayList.add(3, getPackageCount() - getDisplayCount());
        return arrayList;
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
}

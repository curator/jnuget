package ru.aristar.jnuget.rss;

import java.util.Arrays;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class PackageIdAndVersionComparatorTest {

    /**
     * Создает запись о пакете
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return запись о пакете
     * @throws Exception ошибка преобразования версии
     */
    private PackageEntry createPackageEntry(String id, String version) throws Exception {
        PackageEntry entry = new PackageEntry();
        entry.setTitle(id);
        entry.getProperties().setVersion(Version.parse(version));
        return entry;
    }

    /**
     * Проверка корректности работы компаратора на основе сортировки массивов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testOrderPackages() throws Exception {
        //GIVEN
        PackageEntry[] orderedEntrys = new PackageEntry[7];
        orderedEntrys[0] = createPackageEntry("A", "1.2.6");
        orderedEntrys[1] = createPackageEntry("A", "1.2.5");
        orderedEntrys[2] = createPackageEntry("A", "1.2.4");
        orderedEntrys[3] = createPackageEntry("A", "1.2.3");
        orderedEntrys[4] = createPackageEntry("B", "0.2.8");
        orderedEntrys[5] = createPackageEntry("B", "0.2.7");
        orderedEntrys[6] = createPackageEntry("B", "0.2.6");
        PackageEntry[] unOrderedEntrys = new PackageEntry[7];
        unOrderedEntrys[0] = orderedEntrys[1];
        unOrderedEntrys[1] = orderedEntrys[6];
        unOrderedEntrys[2] = orderedEntrys[5];
        unOrderedEntrys[3] = orderedEntrys[4];
        unOrderedEntrys[4] = orderedEntrys[0];
        unOrderedEntrys[5] = orderedEntrys[2];
        unOrderedEntrys[6] = orderedEntrys[3];
        //WHEN
        Arrays.sort(unOrderedEntrys, new PackageIdAndVersionComparator());
        //THEN
        assertArrayEquals("Список должен быть корректно сортирован", orderedEntrys, unOrderedEntrys);
    }
}

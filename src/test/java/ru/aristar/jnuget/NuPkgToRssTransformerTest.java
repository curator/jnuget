package ru.aristar.jnuget;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.aristar.jnuget.rss.PackageEntry;

/**
 *
 * @author sviridov
 */
public class NuPkgToRssTransformerTest {

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
     * Проверка маркировки сортированного списка
     *
     * @throws Exception
     */
    @Test
    public void testMarkLastVersion() throws Exception {
        //GIVEN        
        NuPkgToRssTransformer transformer = new NuPkgToRssTransformer(null);
        ArrayList<PackageEntry> entrys = new ArrayList<>();
        PackageEntry firstA = createPackageEntry("A", "1.2.3");
        entrys.add(firstA);
        entrys.add(createPackageEntry("A", "1.2.4"));
        entrys.add(createPackageEntry("A", "1.2.5"));
        PackageEntry lastA = createPackageEntry("A", "1.2.6");
        entrys.add(lastA);
        PackageEntry firstB = createPackageEntry("B", "0.2.6");
        entrys.add(firstB);
        entrys.add(createPackageEntry("B", "0.2.7"));
        PackageEntry lastB = createPackageEntry("B", "0.2.8");
        entrys.add(lastB);
        //WHEN
        transformer.markLastVersion(entrys);
        //THEN
        assertFalse("Меньшие версии не последние", firstA.getProperties().getIsLatestVersion());
        assertFalse("Меньшие версии не последние", firstB.getProperties().getIsLatestVersion());
        assertTrue("Большие версии последние", lastA.getProperties().getIsLatestVersion());
        assertTrue("Большие версии последние", lastB.getProperties().getIsLatestVersion());
    }
}

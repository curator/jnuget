package ru.aristar.jnuget.rss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import static org.hamcrest.CoreMatchers.*;
import java.util.List;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.NugetContext;
import ru.aristar.jnuget.Version;

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
        NuPkgToRssTransformer transformer = new NuPkgToRssTransformerNoContext();
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

    /**
     * Тест получения подсписка для значений skip=0, top=-1
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetSublist() throws Exception {
        //GIVEN
        List<Object> sources = new ArrayList<>();
        sources.addAll(Collections.nCopies(10, new Object()));
        NuPkgToRssTransformer transformer = new NuPkgToRssTransformerNoContext();
        //WHEN
        List<Object> result = transformer.cutPackageList(0, -1, sources);
        //THEN
        assertEquals("Размер обрезанного списка", 10, result.size());
    }

    /**
     * Проверка усечения списка пакетов для случая, когда количество
     * пропускаемых пакетов равно количеству объектов
     */
    @Test
    public void testCutPackageList() {
        //GIVEN
        Object[] objects = new Object[30];
        Arrays.fill(objects, new Object());
        List<Object> sources = new ArrayList<>();
        sources.addAll(Arrays.asList(objects));
        NuPkgToRssTransformer transformer = new NuPkgToRssTransformerNoContext();
        //WHEN
        List<Object> result = transformer.cutPackageList(30, 0, sources);
        //THEN
        assertThat(result.size(), is(equalTo(0)));
    }

    /**
     * Тест получения подсписка для значений skip=0, top=-1
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetSublistFromOneMessageList() throws Exception {
        //GIVEN
        List<Object> sources = new ArrayList<>();
        sources.add(new Object());
        NuPkgToRssTransformer transformer = new NuPkgToRssTransformerNoContext();
        //WHEN
        List<Object> result = transformer.cutPackageList(0, -1, sources);
        //THEN
        assertEquals("Размер обрезанного списка", 1, result.size());
    }

    /**
     * Проверка получения компаратора по умолчанию
     */
    @Test
    public void testGetDefaultTransformer() {
        //GIVEN
        NuPkgToRssTransformer transformer = new NuPkgToRssTransformerNoContext();
        //WHEN
        Comparator<PackageEntry> result = transformer.getPackageComparator(null);
        //THEN
        assertThat("Компаратор по умолчанию", result, is(instanceOf(PackageIdAndVersionComparator.class)));
    }

    /**
     * Преобразователь пакетов в RSS, не поддерживающий операции с контекстом
     */
    public static class NuPkgToRssTransformerNoContext extends NuPkgToRssTransformer {

        @Override
        protected NugetContext getContext() {
            throw new UnsupportedOperationException("Тестовый класс не поддерживает этот метод");
        }
    }
}

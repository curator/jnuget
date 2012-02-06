package ru.aristar.jnuget.sources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
 * Тесты для индекса пакетов
 *
 * @author sviridov
 */
public class IndexTest {

    /**
     * Контекст для создания заглушек
     */
    private Mockery context = new Mockery();
    /**
     * Уникальный идентификатор заглушки
     */
    private int mockId = 0;

    /**
     * Создает идентификатор фала пакета
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return идентификатор фала пакета
     * @throws Exception некорректный формат версии
     */
    private Nupkg createNupkg(final String id, final String version) throws Exception {
        final Nupkg pack = context.mock(Nupkg.class, "nupkg" + (mockId++));
        context.checking(new Expectations() {

            {
                atLeast(0).of(pack).getId();
                will(returnValue(id));
                atLeast(0).of(pack).getVersion();
                will(returnValue(Version.parse(version)));
            }
        });

        return pack;
    }

    /**
     * Сортирует массив пакетов сначало по возрастанию идентификатора пакета,
     * затем по возрастанию его версии
     *
     * @param result сортированный массив
     */
    private void sortNupkgArray(Nupkg[] result) {
        Arrays.sort(result, new Comparator<Nupkg>() {

            @Override
            public int compare(Nupkg o1, Nupkg o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
    }

    /**
     * Преобразует итератор в массив элементов
     *
     * @param iterator итератор
     * @return массив элементов
     */
    private Nupkg[] iteratorToArray(Iterator<Nupkg> iterator) {
        ArrayList<Nupkg> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result.toArray(new Nupkg[0]);
    }

    /**
     * Проверка получения списка всех пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetAllPackages() throws Exception {
        //GIVEN
        Nupkg[] nupkgs = new Nupkg[]{
            createNupkg("A", "1.1.0"),
            createNupkg("A", "1.2.0"),
            createNupkg("B", "1.1.0"),
            createNupkg("C", "2.1.0"),
            createNupkg("C", "5.1.0")
        };

        Index index = new Index();
        index.putAll(nupkgs);

        //WHEN
        Nupkg[] result = iteratorToArray(index.getAllPackages());
        //THEN
        sortNupkgArray(result);
        assertArrayEquals("Полный список пакетов в индексе", nupkgs, result);
    }

    /**
     * Проверка получения последних версий пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetLastVersions() throws Exception {
        //GIVEN
        Nupkg lastA, lastB, lastC;
        Nupkg[] nupkgs = new Nupkg[]{
            createNupkg("A", "1.1.0"),
            lastA = createNupkg("A", "1.2.0"),
            lastB = createNupkg("B", "1.1.0"),
            createNupkg("C", "2.1.0"),
            lastC = createNupkg("C", "5.1.0")
        };

        Index index = new Index();
        index.putAll(nupkgs);

        //WHEN
        Nupkg[] result = iteratorToArray(index.getLastVersions());
        //THEN
        sortNupkgArray(result);
        assertArrayEquals("Последние версии пакетов", new Nupkg[]{lastA, lastB, lastC}, result);
    }
}

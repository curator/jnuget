package ru.aristar.jnuget.sources;

import java.util.ArrayList;
import java.util.Iterator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
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
        Iterator<Nupkg> i = index.getAllPackages();
        //THEN
        ArrayList<Nupkg> result = new ArrayList<>();
        while (i.hasNext()) {
            result.add(i.next());
        }
        assertEquals("Количество пакетов в индексе", nupkgs.length, result.size());
    }

    @Test
    public void testGetLastVersions() throws Exception {
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
        Iterator<Nupkg> i = index.getLastVersions();
        //THEN
        ArrayList<Nupkg> result = new ArrayList<>();
        while (i.hasNext()) {
            result.add(i.next());
        }
        assertEquals("Количество пакетов в индексе", 3, result.size());
    }
}

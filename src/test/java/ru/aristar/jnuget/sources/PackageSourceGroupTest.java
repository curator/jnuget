package ru.aristar.jnuget.sources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class PackageSourceGroupTest {

    /**
     * Mock контекст
     */
    private Mockery context = new Mockery();
    /**
     * Идентификатор заглушки
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
     * Проверка получения полного списка пакетов
     */
    @Test
    public void testProxyGetPackages() {
        //GIVEN    
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {
            {
                //THEN
                oneOf(source).getPackages();
                will(returnValue(new ArrayList<Nupkg>()));
            }
        });
        PackageSourceGroup packageSource = new PackageSourceGroup();
        packageSource.getSources().add(source);
        //WHEN
        packageSource.getPackages();
    }

    /**
     * Проверка получения последних версий пакетов из всех хранилищ
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetLastVersions() throws Exception {
        //GIVEN
        PackageSourceGroup rootPackageSource = new PackageSourceGroup();
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source1 = context.mock(PackageSource.class, "source1");
        context.checking(new Expectations() {
            {
                atLeast(0).of(source1).getLastVersionPackages();
                will(returnValue(Arrays.asList(createNupkg("A", "1.2.3"))));
            }
        });

        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source2 = context.mock(PackageSource.class, "source2");
        context.checking(new Expectations() {
            {
                atLeast(0).of(source2).getLastVersionPackages();
                will(returnValue(Arrays.asList(createNupkg("A", "1.2.4"))));
            }
        });

        rootPackageSource.getSources().add(source1);
        rootPackageSource.getSources().add(source2);
        //WHEN
        Collection<Nupkg> result = rootPackageSource.getLastVersionPackages();
        //THEN
        assertEquals("Количкство пакетов", 1, result.size());
        Nupkg nupkg = result.iterator().next();
        assertEquals("Идентификатор пакета", "A", nupkg.getId());
        assertEquals("Версия пакета", Version.parse("1.2.4"), nupkg.getVersion());

    }
}

package ru.aristar.jnuget.sources;

import java.util.ArrayList;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class RootPackageSourceTest {

    /**
     * Mock контекст
     */
    private Mockery context = new Mockery();

    /**
     * Проверка получения полного списка пакетов
     */
    @Test
    public void testProxyGetPackages() {
        //GIVEN        
        final PackageSource source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                //THEN
                oneOf(source).getPackages();
                will(returnValue(new ArrayList<Nupkg>()));
            }
        });
        RootPackageSource packageSource = new RootPackageSource();
        packageSource.getSources().add(source);
        //WHEN
        packageSource.getPackages();
    }
}

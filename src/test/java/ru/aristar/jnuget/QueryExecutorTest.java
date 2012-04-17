package ru.aristar.jnuget;

import java.util.Arrays;
import java.util.Collection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;
import static org.jmock.Expectations.*;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 * Тест класса, ограничивающего выборку пакетов
 *
 * @author sviridov
 */
public class QueryExecutorTest {

    /**
     * Контекст создания заглушек
     */
    private Mockery context = new Mockery();

    /**
     * Проверка получения списка пакетов для пустого условия
     */
    @Test
    public void testExecQueryWithNull() {
        //GIVEN
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                oneOf(source).getPackages();
            }
        });
        QueryExecutor executor = new QueryExecutor();
        //WHEN
        executor.execQuery(source, null);
        //THEN
        context.assertIsSatisfied();
    }

    /**
     * Проверка получения списка пакетов для фиксированной версии
     */
    @Test
    public void testExecQueryWithId() {
        //GIVEN
        final String filter = "tolower(id) eq 'package.name'";
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                oneOf(source).getPackages("package.name", true);
            }
        });
        QueryExecutor executor = new QueryExecutor();
        //WHEN
        executor.execQuery(source, filter);
        //THEN
        context.assertIsSatisfied();
    }

    /**
     * Проверка получения списка пакетов последней версии
     */
    @Test
    public void testExecQueryWithLastVersion() {
        //GIVEN
        final String filter = "isLatestVersion";
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                oneOf(source).getLastVersionPackages();
            }
        });
        QueryExecutor executor = new QueryExecutor();
        //WHEN
        executor.execQuery(source, filter);
        //THEN
        context.assertIsSatisfied();
    }

    /**
     * Проверка поиска версии с условием поиска
     *
     * @throws NugetFormatException тестовый формат версии не соответствует
     * формату
     */
    @Test
    public void testExecQueryWithSearchTerm() throws NugetFormatException {
        //GIVEN
        QueryExecutor executor = new QueryExecutor();
        final String searchTerm = "id";
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                atLeast(0).of(source).getPackages();
                will(returnValue(Arrays.asList(
                        createPackageStub("id1", "1.0.1"),
                        createPackageStub("aaaa", "1.2.3"))));
            }
        });
        //WHEN
        Collection<Nupkg> result = executor.execQuery(source, null, searchTerm);
        Nupkg[] nupkgs = result.toArray(new Nupkg[0]);
        //THEN
        assertThat("Количество отфильтрованных пакетов", nupkgs.length, is(equal(1)));
        assertThat("Идентификатор пакета", nupkgs[0].getId(), is(equalTo("id1")));
        assertThat("Версия пакета", nupkgs[0].getVersion(), is(equalTo(Version.parse("1.0.1"))));
    }

    /**
     * Проверка поиска версии с null условием поиска
     *
     * @throws NugetFormatException тестовый формат версии не соответствует
     * формату
     */
    @Test
    public void testExecQueryWithNullSearchTerm() throws NugetFormatException {
        //GIVEN
        QueryExecutor executor = new QueryExecutor();
        final String searchTerm = null;
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                atLeast(0).of(source).getPackages();
                will(returnValue(Arrays.asList(
                        createPackageStub("id1", "1.0.1"),
                        createPackageStub("aaaa", "1.2.3"))));
            }
        });
        //WHEN
        Collection<Nupkg> result = executor.execQuery(source, null, searchTerm);
        Nupkg[] nupkgs = result.toArray(new Nupkg[0]);
        //THEN
        assertThat("Количество отфильтрованных пакетов", nupkgs.length, is(equal(2)));
        assertThat("Идентификатор пакета", nupkgs[0].getId(), is(equalTo("id1")));
        assertThat("Версия пакета", nupkgs[0].getVersion(), is(equalTo(Version.parse("1.0.1"))));
        assertThat("Идентификатор пакета", nupkgs[1].getId(), is(equalTo("aaaa")));
        assertThat("Версия пакета", nupkgs[1].getVersion(), is(equalTo(Version.parse("1.2.3"))));
    }

    /**
     * Проверка поиска версии с условием поиска содержащим только пробелы
     *
     * @throws NugetFormatException тестовый формат версии не соответствует
     * формату
     */
    @Test
    public void testExecQueryWithBlankSearchTerm() throws NugetFormatException {
        //GIVEN
        QueryExecutor executor = new QueryExecutor();
        final String searchTerm = "         ";
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                atLeast(0).of(source).getPackages();
                will(returnValue(Arrays.asList(
                        createPackageStub("id1", "1.0.1"),
                        createPackageStub("aaaa", "1.2.3"))));
            }
        });
        //WHEN
        Collection<Nupkg> result = executor.execQuery(source, null, searchTerm);
        Nupkg[] nupkgs = result.toArray(new Nupkg[0]);
        //THEN
        assertThat("Количество отфильтрованных пакетов", nupkgs.length, is(equal(2)));
        assertThat("Идентификатор пакета", nupkgs[0].getId(), is(equalTo("id1")));
        assertThat("Версия пакета", nupkgs[0].getVersion(), is(equalTo(Version.parse("1.0.1"))));
        assertThat("Идентификатор пакета", nupkgs[1].getId(), is(equalTo("aaaa")));
        assertThat("Версия пакета", nupkgs[1].getVersion(), is(equalTo(Version.parse("1.2.3"))));
    }

    /**
     * Проверка поиска версии с условием поиска содержащим только пробелы
     *
     * @throws NugetFormatException тестовый формат версии не соответствует
     * формату
     */
    @Test
    public void testExecQueryWithQuotesSearchTerm() throws NugetFormatException {
        //GIVEN
        QueryExecutor executor = new QueryExecutor();
        final String searchTerm = "'id'";
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                atLeast(0).of(source).getPackages();
                will(returnValue(Arrays.asList(
                        createPackageStub("id1", "1.0.1"),
                        createPackageStub("aaaa", "1.2.3"))));
            }
        });
        //WHEN
        Collection<Nupkg> result = executor.execQuery(source, null, searchTerm);
        Nupkg[] nupkgs = result.toArray(new Nupkg[0]);
        //THEN
        assertThat("Количество отфильтрованных пакетов", nupkgs.length, is(equal(1)));
        assertThat("Идентификатор пакета", nupkgs[0].getId(), is(equalTo("id1")));
        assertThat("Версия пакета", nupkgs[0].getVersion(), is(equalTo(Version.parse("1.0.1"))));
    }

    /**
     * Создание заглушки пакета
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return заглушка пакета
     * @throws NugetFormatException версиz не соответствует формату
     */
    private Nupkg createPackageStub(String id, String version) throws NugetFormatException {
        Nupkg result = context.mock(Nupkg.class, id + ":" + version);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(result).getId();
        expectations.will(returnValue(id));
        expectations.atLeast(0).of(result).getVersion();
        expectations.will(returnValue(Version.parse(version)));
        context.checking(expectations);
        return result;

    }
}

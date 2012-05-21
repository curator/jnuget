package ru.aristar.jnuget;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import static org.hamcrest.CoreMatchers.*;
import org.jmock.Expectations;
import static org.jmock.Expectations.equal;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import ru.aristar.jnuget.files.Framework;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.sources.PackageSource;

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
        Expectations expectations = new Expectations();
        expectations.oneOf(source).getPackages("package.name");
        context.checking(expectations);
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
     * @throws NugetFormatException версия заглушки пакета не соответствует
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
        Collection<? extends Nupkg> result = executor.execQuery(source, null, searchTerm);
        Nupkg[] nupkgs = result.toArray(new Nupkg[0]);
        //THEN
        assertThat("Количество отфильтрованных пакетов", nupkgs.length, is(equal(1)));
        assertThat("Идентификатор пакета", nupkgs[0].getId(), is(equalTo("id1")));
        assertThat("Версия пакета", nupkgs[0].getVersion(), is(equalTo(Version.parse("1.0.1"))));
    }

    /**
     * Проверка поиска версии с условием поиска в верхнем регистре
     *
     * @throws NugetFormatException версия заглушки пакета не соответствует
     * формату
     */
    @Test
    public void testExecQueryWithSearchTermInUpperCase() throws NugetFormatException {
        //GIVEN
        QueryExecutor executor = new QueryExecutor();
        final String searchTerm = "ID";
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
        Collection<? extends Nupkg> result = executor.execQuery(source, null, searchTerm);
        Nupkg[] nupkgs = result.toArray(new Nupkg[0]);
        //THEN
        assertThat("Количество отфильтрованных пакетов", nupkgs.length, is(equal(1)));
        assertThat("Идентификатор пакета", nupkgs[0].getId(), is(equalTo("id1")));
        assertThat("Версия пакета", nupkgs[0].getVersion(), is(equalTo(Version.parse("1.0.1"))));
    }

    /**
     * Проверка поиска версии с null условием поиска
     *
     * @throws NugetFormatException версия заглушки пакета не соответствует
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
        Collection<? extends Nupkg> result = executor.execQuery(source, null, searchTerm);
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
     * @throws NugetFormatException версия заглушки пакета не соответствует
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
        Collection<? extends Nupkg> result = executor.execQuery(source, null, searchTerm);
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
     * @throws NugetFormatException версия заглушки пакета не соответствует
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
        Collection<? extends Nupkg> result = executor.execQuery(source, null, searchTerm);
        Nupkg[] nupkgs = result.toArray(new Nupkg[0]);
        //THEN
        assertThat("Количество отфильтрованных пакетов", nupkgs.length, is(equal(1)));
        assertThat("Идентификатор пакета", nupkgs[0].getId(), is(equalTo("id1")));
        assertThat("Версия пакета", nupkgs[0].getVersion(), is(equalTo(Version.parse("1.0.1"))));
    }

    /**
     * @throws NugetFormatException версия заглушки пакета не соответствует
     * формату
     */
    @Test
    public void testGetWithFrameworkSpecification() throws NugetFormatException {
        //GIVEN
        QueryExecutor executor = new QueryExecutor();
        Nupkg package1 = createPackageStub("id1", "1.0.1");
        Nupkg package2 = createPackageStub("id2", "1.2.3");

        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(source).getPackages();
        expectations.will(returnValue(Arrays.asList(package1, package2)));
        expectations.atLeast(0).of(package1).getTargetFramework();
        expectations.will(returnValue(EnumSet.allOf(Framework.class)));
        expectations.atLeast(0).of(package2).getTargetFramework();
        expectations.will(returnValue(EnumSet.of(Framework.net20)));
        context.checking(expectations);
        //WHEN
        @SuppressWarnings("unchecked")
        Collection<Nupkg> result = (Collection<Nupkg>) executor.execQuery(source, null, null, "NET11");
        //THEN
        assertThat(result, is(hasItems(package1)));
        assertThat(result, is(not(hasItem(package2))));
    }

    /**
     * Проверка получения данных для реального запроса
     *
     * @throws NugetFormatException версия заглушки пакета не соответствует
     * формату
     */
    @Test
    public void testGetRealQuerry() throws NugetFormatException {
        //GIVEN
        //Строка запроса
        final String queryString = "((((((tolower(Id) eq 'first.package') or "
                + "(tolower(Id) eq 'second.package')) "
                + "or (tolower(Id) eq 'thrid.package')) "
                + "or (tolower(Id) eq 'fourth.package')) "
                + "or (tolower(Id) eq 'fifth.package')) "
                + "or (tolower(Id) eq 'sixth.package')) "
                + "or (tolower(Id) eq 'seventh.package') "
                + "and isLatestVersion";
        //Пакеты
        Nupkg firstPackage = createPackageStub("first.package", "1");
        Nupkg secondPackage = createPackageStub("second.package", "1");
        Nupkg thridPackage = createPackageStub("thrid.package", "1");
        Nupkg fourthPackage = createPackageStub("fourth.package", "1");
        Nupkg fifthPackage = createPackageStub("fifth.package", "1");
        Nupkg sixthPackage = createPackageStub("sixth.package", "1");
        Nupkg seventhFirstPackage = createPackageStub("seventh.package", "1");
        Nupkg seventhLastPackage = createPackageStub("seventh.package", "2");
        Nupkg eighthPackage = createPackageStub("eighth.package", "1");
        //Хранилище
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        Expectations expectations = new Expectations();
        expectations.oneOf(source).getPackages("first.package");
        expectations.will(returnValue(Arrays.asList(firstPackage)));
        expectations.oneOf(source).getPackages("second.package");
        expectations.will(returnValue(Arrays.asList(secondPackage)));
        expectations.oneOf(source).getPackages("thrid.package");
        expectations.will(returnValue(Arrays.asList(thridPackage)));
        expectations.oneOf(source).getPackages("fourth.package");
        expectations.will(returnValue(Arrays.asList(fourthPackage)));
        expectations.oneOf(source).getPackages("fifth.package");
        expectations.will(returnValue(Arrays.asList(fifthPackage)));
        expectations.oneOf(source).getPackages("sixth.package");
        expectations.will(returnValue(Arrays.asList(sixthPackage)));
        expectations.oneOf(source).getPackages("seventh.package");
        expectations.will(returnValue(Arrays.asList(seventhFirstPackage, seventhLastPackage)));
        expectations.oneOf(source).getLastVersionPackages();
        expectations.will(returnValue(Arrays.asList(firstPackage, secondPackage,
                thridPackage, fourthPackage, fifthPackage,
                sixthPackage, seventhLastPackage, eighthPackage)));

        context.checking(expectations);
        //WHEN
        QueryExecutor executor = new QueryExecutor();
        Collection<? extends Nupkg> result = executor.execQuery(source, queryString, null);
        //THEN
        Nupkg[] expecteds = {firstPackage, secondPackage, thridPackage,
            fourthPackage, fifthPackage, sixthPackage, seventhLastPackage};
        assertArrayEquals("Пакеты, полученные из хранилища", expecteds, result.toArray(new Nupkg[0]));
    }

    /**
     * Проверка получения списка пакетов на реальном пакете
     *
     * @throws IOException ошибка чтения тела пакета
     * @throws NugetFormatException тестовый пакет не соответствует формату
     * NuGet
     */
    @Test
    public void testGetPackagesWithDirectFrameWork() throws IOException, NugetFormatException {
        //GIVEN
        TempNupkgFile nupkg = new TempNupkgFile(this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg"));
        @SuppressWarnings("unchecked")
        final PackageSource<Nupkg> source = context.mock(PackageSource.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(source).getLastVersionPackages();
        expectations.will(returnValue(Arrays.asList(nupkg)));
        context.checking(expectations);
        QueryExecutor executor = new QueryExecutor();
        //WHEN
        Collection<? extends Nupkg> nupkgs = executor.execQuery(source, "IsLatestVersion", null, "net20");
        //THEN
        assertThat(nupkgs.size(), is(equalTo(1)));
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

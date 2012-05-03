package ru.aristar.jnuget.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Тесты лексического анализатора запросов
 *
 * @author sviridov
 */
public class QueryLexerTest {

    /**
     * Контекст для создания заглушек
     */
    private Mockery context = new Mockery();

    /**
     * Проверка разделения строки на токены
     */
    @Test
    public void testTokenizeString() {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "((((((tolower(Id) eq 'projectwise.api') "
                + "or (tolower(Id) eq 'projectwise.api')) or "
                + "(tolower(Id) eq 'projectwise.controls')) or "
                + "(tolower(Id) eq 'projectwise.isolationlevel')) or "
                + "(tolower(Id) eq 'projectwise.isolationlevel.implementation')) "
                + "or (tolower(Id) eq 'nlog')) or (tolower(Id) eq 'postsharp') and isLatestVersion";
        //WHEN
        List<String> tokens = lexer.split(filterString);
        String[] actual = tokens.toArray(new String[0]);
        String[] expected = new String[]{"(", "(", "(", "(", "(", "(", "tolower", "(",
            "Id", ")", "eq", "'", "projectwise.api", "'", ")",
            "or", "(", "tolower", "(", "Id", ")", "eq", "'", "projectwise.api", "'", ")", ")", "or",
            "(", "tolower", "(", "Id", ")", "eq", "'", "projectwise.controls", "'", ")", ")", "or",
            "(", "tolower", "(", "Id", ")", "eq", "'", "projectwise.isolationlevel", "'", ")", ")", "or",
            "(", "tolower", "(", "Id", ")", "eq", "'", "projectwise.isolationlevel.implementation", "'", ")", ")",
            "or", "(", "tolower", "(", "Id", ")", "eq", "'", "nlog", "'", ")", ")", "or", "(", "tolower", "(",
            "Id", ")", "eq", "'", "postsharp", "'", ")", "and", "isLatestVersion"};
        //THEN
        assertArrayEquals("Множество токенов", expected, actual);
    }

    /**
     * Проверка простого выражения на эквивалентность идентификатора пакета
     *
     * @throws NugetFormatException строка запроса не соответствует формату
     * NuGet
     */
    @Test
    public void testSimpleEqExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        assertThat("Операция верхнего уровня", expression, is(instanceOf(IdEqIgnoreCase.class)));
        IdEqIgnoreCase eqIgnoreCase = (IdEqIgnoreCase) expression;
        assertThat("Значение параметра", eqIgnoreCase.packageId, is(equalTo("projectwise.api")));
    }

    /**
     * Проверка простого выражения на эквивалентность идентификатора пакета в
     * скобках
     *
     * @throws NugetFormatException строка запроса не соответствует формату
     * NuGet
     */
    @Test
    public void testSimpleGroupEqExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "(tolower(Id) eq 'projectwise.api')";
        //WHEN
        Expression expression = lexer.parse(filterString);
        assertThat("Операция верхнего уровня", expression, is(instanceOf(GroupExpression.class)));
        GroupExpression groupExpression = (GroupExpression) expression;
        Expression level2Expression = groupExpression.innerExpression;
        assertThat("Операция сравнения идентификатора пакета", level2Expression, is(instanceOf(IdEqIgnoreCase.class)));
        IdEqIgnoreCase eqIgnoreCase = (IdEqIgnoreCase) level2Expression;
        assertThat("Значение параметра", eqIgnoreCase.packageId, is(equalTo("projectwise.api")));
    }

    /**
     * Проверка простого выражения на эквивалентность идентификатора и выражения
     * последняя версия
     *
     * @throws NugetFormatException строка запроса не соответствует формату
     * NuGet
     */
    @Test
    public void testLastVersionAndEqExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "tolower(Id) eq 'projectwise.api' and isLatestVersion";
        //WHEN
        Expression expression = lexer.parse(filterString);
        assertThat("Операция верхнего уровня", expression, is(instanceOf(AndExpression.class)));
        AndExpression andExpression = (AndExpression) expression;
        Expression firstExpression = andExpression.firstExpression;
        assertThat("Первая операция второго уровня", firstExpression, is(instanceOf(IdEqIgnoreCase.class)));
        IdEqIgnoreCase firstEqExpression = (IdEqIgnoreCase) firstExpression;
        assertThat("Значение первой операции", firstEqExpression.packageId, is(equalTo("projectwise.api")));

        Expression secondExpression = andExpression.secondExpression;
        assertThat("Вторая операция второго уровня", secondExpression, is(instanceOf(LatestVersionExpression.class)));
    }

    @Test
    public void testOrTwoEqExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "(tolower(Id) eq 'projectwise.api') or (tolower(Id) eq 'projectwise.controls')";
        //WHEN
        Expression expression = lexer.parse(filterString);
        //THEN
        assertThat("Операция верхнего уровня", expression, is(instanceOf(OrExpression.class)));
        OrExpression orExpression = (OrExpression) expression;
        assertThat("Класс первого параметра", orExpression.firstExpression, is(instanceOf(GroupExpression.class)));
        GroupExpression firstGroup = (GroupExpression) orExpression.firstExpression;
        assertThat("Класс первой группы", firstGroup.innerExpression, is(instanceOf(IdEqIgnoreCase.class)));
        IdEqIgnoreCase firstEq = (IdEqIgnoreCase) firstGroup.innerExpression;
        assertThat("Значение параметра", firstEq.packageId, is(equalTo("projectwise.api")));

        assertThat("Класс второго параметра", orExpression.secondExpression, is(instanceOf(GroupExpression.class)));
        GroupExpression secondGroup = (GroupExpression) orExpression.secondExpression;
        assertThat("Класс второй группы", secondGroup.innerExpression, is(instanceOf(IdEqIgnoreCase.class)));
        IdEqIgnoreCase secondEq = (IdEqIgnoreCase) secondGroup.innerExpression;
        assertThat("Значение параметра", secondEq.packageId, is(equalTo("projectwise.controls")));
    }

    @Test
    public void testMultipleAndOrExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "tolower(Id) eq 'projectwise.api' or tolower(Id) eq 'projectwise.controls' "
                + "and tolower(Id) eq 'projectwise.isolationlevel'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        //THEN
        assertThat("Операция первого уровня", expression, is(instanceOf(OrExpression.class)));
        OrExpression orExpression = (OrExpression) expression;
        assertThat("Первое слогаемое", orExpression.firstExpression,
                is(instanceOf(IdEqIgnoreCase.class)));
        assertThat("Первый идентификатор", ((IdEqIgnoreCase) orExpression.firstExpression).packageId,
                is(equalTo("projectwise.api")));
        assertThat("Второе слогаемое", orExpression.secondExpression,
                is(instanceOf(AndExpression.class)));
        AndExpression andExpression = (AndExpression) orExpression.secondExpression;
        assertThat("Умножаемое", andExpression.firstExpression, is(instanceOf(IdEqIgnoreCase.class)));
        assertThat("Второй идентификатор", ((IdEqIgnoreCase) andExpression.firstExpression).packageId,
                is(equalTo("projectwise.controls")));
        assertThat("Множитель", andExpression.secondExpression, is(instanceOf(IdEqIgnoreCase.class)));
        assertThat("Второй идентификатор", ((IdEqIgnoreCase) andExpression.secondExpression).packageId,
                is(equalTo("projectwise.isolationlevel")));
    }

    @Test
    public void testOrAndMultipleExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "tolower(Id) eq 'projectwise.api' and tolower(Id) eq 'projectwise.controls' "
                + "or tolower(Id) eq 'projectwise.isolationlevel'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        //THEN
        assertThat("Операция первого уровня", expression, is(instanceOf(OrExpression.class)));
        OrExpression orExpression = (OrExpression) expression;
        assertThat("Первое слогаемое", orExpression.secondExpression,
                is(instanceOf(IdEqIgnoreCase.class)));
        assertThat("Первый идентификатор", ((IdEqIgnoreCase) orExpression.secondExpression).packageId,
                is(equalTo("projectwise.isolationlevel")));
        assertThat("Второе слогаемое", orExpression.firstExpression,
                is(instanceOf(AndExpression.class)));
        AndExpression andExpression = (AndExpression) orExpression.firstExpression;
        assertThat("Умножаемое", andExpression.firstExpression, is(instanceOf(IdEqIgnoreCase.class)));
        assertThat("Второй идентификатор", ((IdEqIgnoreCase) andExpression.firstExpression).packageId,
                is(equalTo("projectwise.api")));
        assertThat("Множитель", andExpression.secondExpression, is(instanceOf(IdEqIgnoreCase.class)));
        assertThat("Второй идентификатор", ((IdEqIgnoreCase) andExpression.secondExpression).packageId,
                is(equalTo("projectwise.controls")));
    }

    /**
     * Проверка поиска пакетов, с идентификатором, удовлетворяющему условию
     * поиска
     *
     * @throws NugetFormatException строка запроса не соответствует формату
     * NuGet
     */
    @Test
    public void testFindOneIdPackage() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        @SuppressWarnings("unchecked")
        PackageSource<? extends Nupkg> packageSource = context.mock(PackageSource.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(packageSource).getPackages("projectwise.api");
        Nupkg pwPackage = context.mock(Nupkg.class);
        expectations.will(returnValue(Arrays.asList(pwPackage)));
        context.checking(expectations);
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        Collection<? extends Nupkg> result = expression.execute(packageSource);
        //THEN
        assertArrayEquals(new Nupkg[]{pwPackage}, result.toArray(new Nupkg[0]));
    }

    /**
     * Проверка поиска двух пакетов, с идентификатором, удовлетворяющему условию
     * поиска
     *
     * @throws NugetFormatException строка запроса не соответствует формату
     * NuGet
     */
    @Test
    public void testFindTwoOrByIdPackage() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();

        //Пакеты
        Nupkg firstPackage = context.mock(Nupkg.class, "first.package");
        Nupkg secondPackage = context.mock(Nupkg.class, "second.package");

        //Источник пакетов
        @SuppressWarnings("unchecked")
        PackageSource<? extends Nupkg> packageSource = context.mock(PackageSource.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(packageSource).getPackages("first.package");
        expectations.will(returnValue(Arrays.asList(firstPackage)));
        expectations.atLeast(0).of(packageSource).getPackages("second.package");
        expectations.will(returnValue(Arrays.asList(secondPackage)));
        context.checking(expectations);

        final String filterString = "tolower(Id) eq 'first.package' or tolower(Id) eq 'second.package'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        Collection<? extends Nupkg> result = expression.execute(packageSource);
        //THEN
        Nupkg[] expected = {firstPackage, secondPackage};
        assertArrayEquals("Список пакетов", expected, result.toArray(new Nupkg[0]));
    }

    /**
     * Проверка поиска пакета, с идентификатором, удовлетворяющему условию
     * поиска и имеющего последнюю версию
     *
     * @throws NugetFormatException строка запроса не соответствует формату
     * NuGet
     */
    @Test
    public void testFindLastVersionByIdPackage() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();

        //Пакеты
        Nupkg firstPackage = context.mock(Nupkg.class, "first.package:1.1.2");
        Nupkg secondPackage = context.mock(Nupkg.class, "first.package:2.0.0");

        //Источник пакетов
        @SuppressWarnings("unchecked")
        PackageSource<? extends Nupkg> packageSource = context.mock(PackageSource.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(packageSource).getPackages("first.package");
        expectations.will(returnValue(Arrays.asList(firstPackage, secondPackage)));
        expectations.atLeast(0).of(packageSource).getLastVersionPackages();
        expectations.will(returnValue(Arrays.asList(secondPackage)));
        context.checking(expectations);

        final String filterString = "tolower(Id) eq 'first.package' and isLatestVersion";
        //WHEN
        Expression expression = lexer.parse(filterString);
        Collection<? extends Nupkg> result = expression.execute(packageSource);
        //THEN
        assertArrayEquals(new Nupkg[]{secondPackage}, result.toArray(new Nupkg[0]));
    }
}

package ru.aristar.jnuget.query;

import java.util.Arrays;
import java.util.Collection;
import static org.hamcrest.CoreMatchers.*;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.everyItem;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class VersionEqTest {

    /**
     * Контекст для создания заглушек
     */
    private Mockery context = new Mockery();

    /**
     * Проверка преобразования в строку
     *
     * @throws NugetFormatException некорректное значение тестовой версии
     */
    @Test
    public void testToString() throws NugetFormatException {
        //GIVEN
        final Version version = Version.parse("1.2.3");
        //WHEN
        VersionEq versionEq = new VersionEq(version);
        //THEN
        assertThat(versionEq.toString(), is(equalTo("Version eq '1.2.3'")));
    }

    /**
     * Проверка распознавания сравнения версии
     *
     * @throws NugetFormatException некорректное значение тестовой версии или
     * тестовой строки
     */
    @Test
    public void testParse() throws NugetFormatException {
        //GEVIN
        QueryLexer lexer = new QueryLexer();
        //WHEN
        Expression expression = lexer.parse("Version eq '1.2.3'");
        //THEN
        assertThat(expression, is(instanceOf(VersionEq.class)));
        VersionEq versionEq = (VersionEq) expression;
        assertThat(versionEq.getVersion(), is(equalTo(Version.parse("1.2.3"))));
    }

    /**
     * Проверка преобразования сложного выражения к строке
     *
     * @throws NugetFormatException некорректное значение тестовой версии
     */
    @Test
    public void testComplexToString() throws NugetFormatException {
        //GIVEN
        IdEqIgnoreCase eqIgnoreCase = new IdEqIgnoreCase("packageId");
        VersionEq versionEq = new VersionEq(Version.parse("1.2.3"));
        AndExpression andExpression = new AndExpression(eqIgnoreCase, versionEq);
        //WHEN
        String filter = andExpression.toString();
        //THEN
        assertThat(filter, is(equalTo("tolower(Id) eq 'packageid' and Version eq '1.2.3'")));
    }

    /**
     * Проверка выполнения выражения в режиме фильтрации
     *
     * @throws NugetFormatException некорректное значение тестовой версии
     */
    @Test
    public void testFilterExecute() throws NugetFormatException {
        //GIVEN
        IdEqIgnoreCase eqIgnoreCase = new IdEqIgnoreCase("packageId");
        VersionEq versionEq = new VersionEq(Version.parse("1.2.3"));
        AndExpression andExpression = new AndExpression(eqIgnoreCase, versionEq);

        PackageSource packageSource = context.mock(PackageSource.class);
        Nupkg nupkg = context.mock(Nupkg.class);

        Expectations expectations = new Expectations();
        expectations.oneOf(packageSource).getPackages("packageId");
        expectations.will(returnValue(Arrays.asList(nupkg)));

        expectations.oneOf(nupkg).getVersion();
        expectations.will(returnValue(Version.parse("1.2.3")));

        context.checking(expectations);
        //WHEN
        @SuppressWarnings("unchecked")
        Collection<Nupkg> result = (Collection<Nupkg>) andExpression.execute(packageSource);
        //THEN
        assertThat(result.size(), is(equalTo(1)));
        assertThat(result, everyItem(sameInstance(nupkg)));
    }
}

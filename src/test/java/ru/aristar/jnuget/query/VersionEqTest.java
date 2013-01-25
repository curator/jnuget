package ru.aristar.jnuget.query;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 *
 * @author sviridov
 */
public class VersionEqTest {

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
     * @throws NugetFormatException некорректное значение версии
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
}

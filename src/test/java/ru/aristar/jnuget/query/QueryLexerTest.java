package ru.aristar.jnuget.query;

import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 * Тесты лексического анализатора запросов
 *
 * @author sviridov
 */
public class QueryLexerTest {

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

    @Test
    public void testSimpleEqExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        assertThat("Операция верхнего уровня", expression, is(instanceOf(IdEqIgnoreCase.class)));
        IdEqIgnoreCase eqIgnoreCase = (IdEqIgnoreCase) expression;
        assertThat("Значение параметра", eqIgnoreCase.value, is(equalTo("projectwise.api")));
    }

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
        assertThat("Значение параметра", eqIgnoreCase.value, is(equalTo("projectwise.api")));
    }

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
        assertThat("Значение первой операции", firstEqExpression.value, is(equalTo("projectwise.api")));

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
        assertThat("Значение параметра", firstEq.value, is(equalTo("projectwise.api")));

        assertThat("Класс второго параметра", orExpression.secondExpression, is(instanceOf(GroupExpression.class)));
        GroupExpression secondGroup = (GroupExpression) orExpression.secondExpression;
        assertThat("Класс второй группы", secondGroup.innerExpression, is(instanceOf(IdEqIgnoreCase.class)));
        IdEqIgnoreCase secondEq = (IdEqIgnoreCase) secondGroup.innerExpression;
        assertThat("Значение параметра", secondEq.value, is(equalTo("projectwise.controls")));
    }

    @Test
    @Ignore
    public void testMultipleAndOrExpression() throws NugetFormatException {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "tolower(Id) eq 'projectwise.api' or tolower(Id) eq 'projectwise.controls' and tolower(Id) eq 'projectwise.isolationlevel'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        //THEN
        assertThat("Операция первого уровня", expression, is(instanceOf(AndExpression.class)));
        fail("Тест не дописан");
    }
}

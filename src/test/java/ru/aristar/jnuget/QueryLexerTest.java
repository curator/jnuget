package ru.aristar.jnuget;

import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ru.aristar.jnuget.QueryLexer.Expression;
import ru.aristar.jnuget.QueryLexer.GroupExpression;
import ru.aristar.jnuget.QueryLexer.IdEqIgnoreCase;

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
    public void testSimpleEqExpression() {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        //WHEN
        Expression expression = lexer.parse(filterString);
        assertThat("Операция верхнего уровня", expression.getOperation(), is(equalTo(QueryLexer.Operation.EQ)));
        assertThat("Операция сравнения идентификатора пакета", expression, is(instanceOf(QueryLexer.IdEqIgnoreCase.class)));
        QueryLexer.IdEqIgnoreCase eqIgnoreCase = (QueryLexer.IdEqIgnoreCase) expression;
        assertThat("Значение параметра", eqIgnoreCase.value, is(equalTo("projectwise.api")));
    }

    @Test
    public void testSimpleGroupEqExpression() {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "(tolower(Id) eq 'projectwise.api')";
        //WHEN
        Expression expression = lexer.parse(filterString);
        assertThat("Операция верхнего уровня", expression.getOperation(), is(nullValue()));
        assertThat("Операция групппировки", expression, is(instanceOf(QueryLexer.GroupExpression.class)));
        QueryLexer.GroupExpression groupExpression = (QueryLexer.GroupExpression) expression;
        Expression level2Expression = groupExpression.innerExpression;
        assertThat("Операция сравнения идентификатора пакета", level2Expression, is(instanceOf(QueryLexer.IdEqIgnoreCase.class)));
        QueryLexer.IdEqIgnoreCase eqIgnoreCase = (QueryLexer.IdEqIgnoreCase) level2Expression;
        assertThat("Значение параметра", eqIgnoreCase.value, is(equalTo("projectwise.api")));
    }

    @Test
    public void testOrTwoEqExpression() {
        //GIVEN
        QueryLexer lexer = new QueryLexer();
        final String filterString = "(tolower(Id) eq 'projectwise.api') or (tolower(Id) eq 'projectwise.controls')";
        //WHEN
        Expression expression = lexer.parse(filterString);
        assertThat("Операция верхнего уровня", expression.getOperation(), is(equalTo(QueryLexer.Operation.OR)));
        assertThat("Операция сравнения идентификатора пакета", expression, is(instanceOf(QueryLexer.OrExpression.class)));
        QueryLexer.OrExpression orExpression = (QueryLexer.OrExpression) expression;
        assertThat("Значение первого параметра", orExpression.firstExpression.getOperation(), is(nullValue()));
        assertThat("Класс первого параметра", orExpression.firstExpression, is(instanceOf(QueryLexer.GroupExpression.class)));
        GroupExpression firstGroup = (QueryLexer.GroupExpression) orExpression.firstExpression;
        assertThat("Класс первой группы", firstGroup.innerExpression, is(instanceOf(QueryLexer.IdEqIgnoreCase.class)));
        IdEqIgnoreCase firstEq = (QueryLexer.IdEqIgnoreCase) firstGroup.innerExpression;
        assertThat("Значение параметра", firstEq.value, is(equalTo("projectwise.api")));

        assertThat("Значение второго параметра", orExpression.secondExpression.getOperation(), is(nullValue()));
        assertThat("Класс второго параметра", orExpression.secondExpression, is(instanceOf(QueryLexer.GroupExpression.class)));
        GroupExpression secondGroup = (QueryLexer.GroupExpression) orExpression.secondExpression;
        assertThat("Класс второй группы", secondGroup.innerExpression, is(instanceOf(QueryLexer.IdEqIgnoreCase.class)));
        IdEqIgnoreCase secondEq = (QueryLexer.IdEqIgnoreCase) secondGroup.innerExpression;
        assertThat("Значение параметра", secondEq.value, is(equalTo("projectwise.controls")));
    }
}

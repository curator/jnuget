package ru.aristar.jnuget.query;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * Тест класса очереди токенов
 *
 * @author sviridov
 */
public class TokenQueueTest {

    /**
     * Проверка преобразования к типизированному массиву
     */
    @Test
    public void testToArray() {
        //GIVEN
        final String filterString = "((((((tolower(Id) eq 'projectwise.api') "
                + "or (tolower(Id) eq 'projectwise.api')) or "
                + "(tolower(Id) eq 'projectwise.controls')) or "
                + "(tolower(Id) eq 'projectwise.isolationlevel')) or "
                + "(tolower(Id) eq 'projectwise.isolationlevel.implementation')) "
                + "or (tolower(Id) eq 'nlog')) or (tolower(Id) eq 'postsharp') and isLatestVersion";
        TokenQueue tokenQueue = new TokenQueue(filterString);
        //WHEN
        String[] actual = tokenQueue.toArray(new String[0]);
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
     * Проверка преобразования к нетипизированному массиву
     */
    @Test
    public void testToArrayOfObject() {
        //GIVEN
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        TokenQueue tokenQueue = new TokenQueue(filterString);
        //WHEN
        Object[] actual = tokenQueue.toArray(new Object[0]);
        //THEN
        assertThat(actual.length, is(equalTo(8)));
    }

    /**
     * Тест последовательного получения элементов очереди токенов
     */
    @Test
    public void testPool() {
        //GIVEN
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        //WHEN
        TokenQueue tokenQueue = new TokenQueue(filterString);
        //THEN
        assertThat(tokenQueue.poll(), is(equalTo("tolower")));
        assertThat(tokenQueue.poll(), is(equalTo("(")));
        assertThat(tokenQueue.poll(), is(equalTo("Id")));
        assertThat(tokenQueue.poll(), is(equalTo(")")));
        assertThat(tokenQueue.poll(), is(equalTo("eq")));
        assertThat(tokenQueue.poll(), is(equalTo("'")));
        assertThat(tokenQueue.poll(), is(equalTo("projectwise.api")));
        assertThat(tokenQueue.poll(), is(equalTo("'")));
    }

    /**
     * Тест просмотра верхнего элемента из очереди
     */
    @Test
    public void testPeek() {
        //GIVEN
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        //WHEN
        TokenQueue tokenQueue = new TokenQueue(filterString);
        //THEN
        assertThat(tokenQueue.peek(), is(equalTo("tolower")));
        assertThat(tokenQueue.poll(), is(equalTo("tolower")));
        assertThat(tokenQueue.peek(), is(equalTo("(")));
        assertThat(tokenQueue.poll(), is(equalTo("(")));
    }

    /**
     * Проверка метода, определяющего пуста ли очередь токенов
     */
    @Test
    public void testIsEmpty() {
        //GIVEN
        final String filterString = "tolower(Id)";
        //WHEN
        TokenQueue tokenQueue = new TokenQueue(filterString);
        //THEN
        assertThat(tokenQueue.poll(), is(equalTo("tolower")));
        assertThat(tokenQueue.isEmpty(), is(equalTo(false)));
        assertThat(tokenQueue.poll(), is(equalTo("(")));
        assertThat(tokenQueue.isEmpty(), is(equalTo(false)));
        assertThat(tokenQueue.poll(), is(equalTo("Id")));
        assertThat(tokenQueue.isEmpty(), is(equalTo(false)));
        assertThat(tokenQueue.peek(), is(equalTo(")")));
        assertThat(tokenQueue.isEmpty(), is(equalTo(false)));
        assertThat(tokenQueue.poll(), is(equalTo(")")));
        assertThat(tokenQueue.isEmpty(), is(equalTo(true)));
    }

    /**
     * Тест последовательного получения элементов очереди токенов, с
     * разделителями запятыми
     */
    @Test
    public void testPoolWithComma() {
        //GIVEN
        final String filterString = "substringof('spring',tolower(Id)))";
        //WHEN
        TokenQueue tokenQueue = new TokenQueue(filterString);
        //THEN
        assertThat(tokenQueue.poll(), is(equalTo("substringof")));
        assertThat(tokenQueue.poll(), is(equalTo("(")));
        assertThat(tokenQueue.poll(), is(equalTo("'")));
        assertThat(tokenQueue.poll(), is(equalTo("spring")));
        assertThat(tokenQueue.poll(), is(equalTo("'")));
        assertThat(tokenQueue.poll(), is(equalTo("tolower")));
        assertThat(tokenQueue.poll(), is(equalTo("(")));
        assertThat(tokenQueue.poll(), is(equalTo("Id")));
        assertThat(tokenQueue.poll(), is(equalTo(")")));
        assertThat(tokenQueue.poll(), is(equalTo(")")));
        assertThat(tokenQueue.poll(), is(equalTo(")")));
    }
}

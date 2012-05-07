package ru.aristar.jnuget.query;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class TokenQueueTest {
    
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
    
    @Test
    public void testPeek() {
        //GIVEN
        final String filterString = "tolower(Id) eq 'projectwise.api'";
        //WHEN
        TokenQueue tokenQueue = new TokenQueue(filterString);
        //THEN
        assertThat(tokenQueue.peek(), is(equalTo("tolower")));
        assertThat(tokenQueue.poll(), is(equalTo("tolower")));
    }
}

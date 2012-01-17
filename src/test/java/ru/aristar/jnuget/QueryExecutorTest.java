package ru.aristar.jnuget;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class QueryExecutorTest {

    private Mockery context = new Mockery();

    @Test
    public void testExecQueryWithNull() throws Exception {
        //GIVEN
        final PackageSource source = context.mock(PackageSource.class);
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

    @Test
    public void testExecQueryWithId() throws Exception {
        //GIVEN
        final String filter = "tolower(id) eq package.name";
        final PackageSource source = context.mock(PackageSource.class);
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

    @Test
    public void testExecQueryWithLastVersion() throws Exception {
        //GIVEN
        final String filter = "isLatestVersion";
        final PackageSource source = context.mock(PackageSource.class);
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
}

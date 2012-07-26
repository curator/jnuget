package ru.aristar.jnuget.sources.push;

import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class VersionPatternPushStrategyTest {

    /**
     * Контекст создания заглушек
     */
    private Mockery context = new Mockery();
    /**
     * Идентификатор заглушки
     */
    private int mockId = 0;

    /**
     * Создает идентификатор фала пакета
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return идентификатор фала пакета
     * @throws Exception некорректный формат версии
     */
    private Nupkg createNupkg(final String id, final String version) throws Exception {
        final Nupkg pack = context.mock(Nupkg.class, "nupkg" + (mockId++));
        context.checking(new Expectations() {
            {
                atLeast(0).of(pack).getId();
                will(returnValue(id));
                atLeast(0).of(pack).getVersion();
                will(returnValue(Version.parse(version)));
            }
        });

        return pack;
    }

    /**
     * Проверка версий пакета соответствующих шаблону
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testAcceptVersion() throws Exception {
        //GIVEN
        VersionPatternPushStrategy strategy = new VersionPatternPushStrategy();
        //WHEN
        strategy.setPattern("^\\d+\\.\\d+\\.\\d*[02468].*$");
        //THEN
        assertTrue(strategy.canPush(createNupkg("A", "1.2.4.7")));
        assertTrue(strategy.canPush(createNupkg("A", "1.2.6.71")));
        assertTrue(strategy.canPush(createNupkg("A", "1.2.0.7654")));
        assertTrue(strategy.canPush(createNupkg("V", "1.7.0.AAAAA")));
    }

    /**
     * Проверка версий пакета не соответствующих шаблону
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testRejectedVersion() throws Exception {
        //GIVEN
        VersionPatternPushStrategy strategy = new VersionPatternPushStrategy();
        //WHEN
        strategy.setPattern("^\\d+\\.\\d+\\.\\d*[13579].*$");
        //THEN
        assertFalse(strategy.canPush(createNupkg("A", "1.2.4.7")));
        assertFalse(strategy.canPush(createNupkg("A", "1.2.6.71")));
        assertFalse(strategy.canPush(createNupkg("A", "1.2.0.7654")));
        assertFalse(strategy.canPush(createNupkg("V", "1.7.0.AAAAA")));
    }
}

package ru.aristar.jnuget.files;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class DependencyTest {

    /**
     * Проверка метода toString
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToString() throws Exception {
        //GIVEN
        Dependency dependency = new Dependency();
        //WHEN
        dependency.id = "PACKAGE_ID";
        dependency.version = Version.parse("1.2.3");
        //THEN
        assertEquals("toString - конкатенация идентификатора и версии", "PACKAGE_ID:1.2.3", dependency.toString());
    }
}

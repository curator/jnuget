package ru.aristar.jnuget.files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 * Тест класса зависимостей
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
        dependency.versionRange = VersionRange.parse("1.2.3");
        //THEN
        assertEquals("toString - конкатенация идентификатора и версии", "PACKAGE_ID:1.2.3", dependency.toString());
    }

    /**
     * Проверка распознавания зависимости из строки
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParse() throws Exception {
        //GIVEN
        final String dependencyString = "PACKAGE_ID:1.2.3";
        //WHEN
        Dependency result = Dependency.parseString(dependencyString);
        //THEN
        assertEquals("Идентификатор пакета", "PACKAGE_ID", result.id);
        assertEquals("Диапазон версий пакета", VersionRange.parse("1.2.3"), result.versionRange);
    }

    /**
     * Проверка распознавания зависимости из строки для конкретной версии
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseFixedVersionDependency() throws Exception {
        //GIVEN
        final String dependencyString = "PACKAGE_ID:[1.2.3]";
        //WHEN
        Dependency result = Dependency.parseString(dependencyString);
        //THEN
        assertEquals("Идентификатор пакета", "PACKAGE_ID", result.id);
        assertEquals("Диапазон версий пакета", VersionRange.parse("[1.2.3]"), result.versionRange);
    }

    /**
     * Проверка распознавания зависимости из строки для последней версии
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseLastVersionDependency() throws Exception {
        //GIVEN
        final String dependencyString = "PACKAGE_ID:";
        //WHEN
        Dependency result = Dependency.parseString(dependencyString);
        //THEN
        assertEquals("Идентификатор пакета", "PACKAGE_ID", result.id);
        assertTrue("Это последняя версия", result.versionRange.isLatestVersion());
    }

    /**
     * Проверка распознавания зависимости из строки для не релизной версии
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseNonReleaseVersionDependency() throws Exception {
        //GIVEN
        final String dependencyString = "PACKAGE.ID:[3.0.0.1029-rc]";
        //WHEN
        Dependency result = Dependency.parseString(dependencyString);
        //THEN
        assertEquals("Идентификатор пакета", "PACKAGE.ID", result.id);
        assertTrue("Это фиксированная версия", result.versionRange.isFixedVersion());
        assertEquals("Версия пакета", Version.parse("3.0.0.1029-rc"), result.versionRange.getLowVersion());
    }
}

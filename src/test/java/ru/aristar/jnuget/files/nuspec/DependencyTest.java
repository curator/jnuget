package ru.aristar.jnuget.files.nuspec;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Framework;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.VersionRange;

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
     * Проверка разбора строки зависимости для всех фреймворков
     *
     * @throws NugetFormatException строка зависимости не соответствует формату
     * NuGet
     */
    @Test
    public void testParceDependencyAllFrameworks() throws NugetFormatException {
        //GIVEN
        final String dependencyString = "PACKAGE_ID:1.2.3:";
        //WHEN
        Dependency result = Dependency.parseString(dependencyString);
        //THEN
        assertEquals("Идентификатор пакета", "PACKAGE_ID", result.id);
        assertEquals("Диапазон версий пакета", VersionRange.parse("1.2.3"), result.versionRange);
        assertThat(result.framework, is(nullValue()));
    }

    /**
     * Проверка разбора строки зависимости для конкретного фреймворка
     *
     * @throws NugetFormatException строка зависимости не соответствует формату
     * NuGet
     */
    @Test
    public void testParceDependencyFixedFramework() throws NugetFormatException {
        //GIVEN
        final String dependencyString = "PACKAGE_ID:1.2.3:net20";
        //WHEN
        Dependency result = Dependency.parseString(dependencyString);
        //THEN
        assertEquals("Идентификатор пакета", "PACKAGE_ID", result.id);
        assertEquals("Диапазон версий пакета", VersionRange.parse("1.2.3"), result.versionRange);
        assertThat(result.framework, is(equalTo(Framework.net20)));
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

    /**
     * Проверка распознавания зависимости из строки для не релизной версии и
     * незамкнутого сверху интервала
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseNonReleaseDependency() throws Exception {
        //GIVEN
        final String dependencyString = "PACKAGE.ID:[2.5-a,3.0)";
        //WHEN
        Dependency result = Dependency.parseString(dependencyString);
        //THEN
        assertEquals("Идентификатор пакета", "PACKAGE.ID", result.id);
        assertEquals("Нижняя граница диапазона", Version.parse("2.5-a"), result.versionRange.getLowVersion());
        assertEquals("Тип нижней границы диапазона", VersionRange.BorderType.INCLUDE, result.versionRange.getLowBorderType());
        assertEquals("Верхняя граница диапазона", Version.parse("3.0"), result.versionRange.getTopVersion());
        assertEquals("Тип верхней границы диапазона", VersionRange.BorderType.EXCLUDE, result.versionRange.getTopBorderType());
    }
}

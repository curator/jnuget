package ru.aristar.jnuget.files;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class VersionRangeTest {

    /**
     * Тест диапазона с включенными границами
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringDoubleInclude() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(Version.parse("1.2.3"));
        versionRange.setTopVersion(Version.parse("2.3.1"));
        versionRange.setLowBorderType(VersionRange.BorderType.INCLUDE);
        versionRange.setTopBorderType(VersionRange.BorderType.INCLUDE);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "[1.2.3,2.3.1]", result);
    }

    /**
     * Тест диапазона с исключенными границами
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringDoubleExclude() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(Version.parse("1.2.3"));
        versionRange.setTopVersion(Version.parse("2.3.1"));
        versionRange.setLowBorderType(VersionRange.BorderType.EXCLUDE);
        versionRange.setTopBorderType(VersionRange.BorderType.EXCLUDE);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "(1.2.3,2.3.1)", result);
    }

    /**
     * Тест диапазона с одной включенной и одной исключенной границей
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringIncludeExclude() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(Version.parse("1.2.3"));
        versionRange.setTopVersion(Version.parse("2.3.1"));
        versionRange.setLowBorderType(VersionRange.BorderType.INCLUDE);
        versionRange.setTopBorderType(VersionRange.BorderType.EXCLUDE);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "[1.2.3,2.3.1)", result);
    }

    /**
     * Тест диапазона не ограниченного сверху
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringGreaterThanInclude() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(Version.parse("1.2.3"));
        versionRange.setTopVersion(null);
        versionRange.setLowBorderType(VersionRange.BorderType.INCLUDE);
        versionRange.setTopBorderType(null);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "1.2.3", result);
    }

    /**
     * Тест диапазона не ограниченного снизу, включая верхнюю границу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringLesserThanInclude() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(null);
        versionRange.setTopVersion(Version.parse("1.2.3"));
        versionRange.setLowBorderType(null);
        versionRange.setTopBorderType(VersionRange.BorderType.INCLUDE);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "(,1.2.3]", result);
    }

    /**
     * Тест диапазона не ограниченного снизу, включая верхнюю границу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringLesserThanExclude() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(null);
        versionRange.setTopVersion(Version.parse("1.2.3"));
        versionRange.setLowBorderType(null);
        versionRange.setTopBorderType(VersionRange.BorderType.EXCLUDE);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "(,1.2.3)", result);
    }

    /**
     * Тест диапазона для фиксированной версии
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringFixedVersion() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(Version.parse("1.2.3"));
        versionRange.setTopVersion(Version.parse("1.2.3"));
        versionRange.setLowBorderType(VersionRange.BorderType.INCLUDE);
        versionRange.setTopBorderType(VersionRange.BorderType.INCLUDE);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "[1.2.3]", result);
    }

    /**
     * Тест диапазона не ограниченного сверху, не включая границу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringGreaterThanExclude() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(Version.parse("1.2.3"));
        versionRange.setTopVersion(null);
        versionRange.setLowBorderType(VersionRange.BorderType.EXCLUDE);
        versionRange.setTopBorderType(null);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "(1.2.3,)", result);
    }

    /**
     * Тест последней версии
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testToStringLastVersion() throws Exception {
        //GIVEN
        VersionRange versionRange = new VersionRange();
        versionRange.setLowVersion(null);
        versionRange.setTopVersion(null);
        versionRange.setLowBorderType(null);
        versionRange.setTopBorderType(null);
        //WHEN
        String result = versionRange.toString();
        //THEN
        assertEquals("Строка диапазона версий", "", result);
    }
}

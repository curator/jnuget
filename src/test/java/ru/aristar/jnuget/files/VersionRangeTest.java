package ru.aristar.jnuget.files;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
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
     * Тест диапазона не ограниченного снизу, исключая верхнюю границу
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

    /**
     * Тест парсинга диапазона не ограниченного сверху
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseSimpleVersion() throws Exception {
        //GIVEN
        String versionString = "1.0";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertEquals("Нижняя граница", Version.parse(versionString), versionRange.getLowVersion());
        assertEquals("Тип нижней ганицы", VersionRange.BorderType.INCLUDE, versionRange.getLowBorderType());
        assertNull("Верхняя ганица", versionRange.getTopVersion());
        assertNull("Тип верхней ганицы", versionRange.getTopBorderType());
        assertThat("Версия простая", versionRange.isSimpleRange(), equalTo(true));
    }

    /**
     * Тест парсинга диапазона не ограниченного снизу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseLesserThanInclude() throws Exception {
        //GIVEN
        String versionString = "(,1.0]";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertNull("Нижняя граница", versionRange.getLowVersion());
        assertNull("Тип нижней ганицы", versionRange.getLowBorderType());
        assertEquals("Верхняя ганица", Version.parse("1.0"), versionRange.getTopVersion());
        assertEquals("Тип верхней ганицы", VersionRange.BorderType.INCLUDE, versionRange.getTopBorderType());
    }

    /**
     * Тест парсинга диапазона не ограниченного снизу, исключая верхнюю границу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseLesserThanExclude() throws Exception {
        //GIVEN
        String versionString = "(,1.0)";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertNull("Нижняя граница", versionRange.getLowVersion());
        assertNull("Тип нижней ганицы", versionRange.getLowBorderType());
        assertEquals("Верхняя ганица", Version.parse("1.0"), versionRange.getTopVersion());
        assertEquals("Тип верхней ганицы", VersionRange.BorderType.EXCLUDE, versionRange.getTopBorderType());
    }

    /**
     * Тест парсинга диапазона не ограниченного снизу, исключая верхнюю границу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseFixedVersion() throws Exception {
        //GIVEN
        String versionString = "[1.0]";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertEquals("Нижняя граница", Version.parse("1.0"), versionRange.getLowVersion());
        assertEquals("Тип нижней ганицы", VersionRange.BorderType.INCLUDE, versionRange.getLowBorderType());
        assertEquals("Верхняя ганица", Version.parse("1.0"), versionRange.getTopVersion());
        assertEquals("Тип верхней ганицы", VersionRange.BorderType.INCLUDE, versionRange.getTopBorderType());
        assertThat("Версия фиксированная", versionRange.isFixedVersion(), equalTo(true));
    }

    /**
     * Тест парсинга диапазона не ограниченного снизу, исключая верхнюю границу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseGreaterThanExclude() throws Exception {
        //GIVEN
        String versionString = "(1.0,)";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertEquals("Нижняя граница", Version.parse("1.0"), versionRange.getLowVersion());
        assertEquals("Тип нижней ганицы", VersionRange.BorderType.EXCLUDE, versionRange.getLowBorderType());
        assertNull("Верхняя ганица", versionRange.getTopVersion());
        assertNull("Тип верхней ганицы", versionRange.getTopBorderType());
    }

    /**
     * Тест парсинга диапазона с исключенными границами
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseDoubleExclude() throws Exception {
        //GIVEN
        String versionString = "(1.0,2.0)";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertEquals("Нижняя граница", Version.parse("1.0"), versionRange.getLowVersion());
        assertEquals("Тип нижней ганицы", VersionRange.BorderType.EXCLUDE, versionRange.getLowBorderType());
        assertEquals("Верхняя ганица", Version.parse("2.0"), versionRange.getTopVersion());
        assertEquals("Тип верхней ганицы", VersionRange.BorderType.EXCLUDE, versionRange.getTopBorderType());
    }

    /**
     * Тест парсинга диапазона с включенными границами
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseDoubleInclude() throws Exception {
        //GIVEN
        String versionString = "[1.0,2.0]";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertEquals("Нижняя граница", Version.parse("1.0"), versionRange.getLowVersion());
        assertEquals("Тип нижней ганицы", VersionRange.BorderType.INCLUDE, versionRange.getLowBorderType());
        assertEquals("Верхняя ганица", Version.parse("2.0"), versionRange.getTopVersion());
        assertEquals("Тип верхней ганицы", VersionRange.BorderType.INCLUDE, versionRange.getTopBorderType());
    }

    /**
     * Тест парсинга последней версии
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseLatestVersion() throws Exception {
        //GIVEN
        String versionString = "";
        //WHEN
        VersionRange versionRange = VersionRange.parse(versionString);
        //THEN
        assertNull("Нижняя граница", versionRange.getTopVersion());
        assertNull("Тип нижней ганицы", versionRange.getTopBorderType());
        assertNull("Верхняя ганица", versionRange.getTopVersion());
        assertNull("Тип верхней ганицы", versionRange.getTopBorderType());
        assertThat("Версия фиксированная", versionRange.isLatestVersion(), equalTo(true));
    }
}

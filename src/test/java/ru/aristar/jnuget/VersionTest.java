package ru.aristar.jnuget;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 * тесты класса версии
 *
 * @author unlocker
 */
public class VersionTest {

    @Test
    public void testConstructor() throws Exception {
        //GIVEN
        String strVersion = "1.2.3.4";
        //WHEN
        Version version = Version.parse(strVersion);
        //THEN
        assertEquals("Major", Integer.valueOf(1), version.getMajor());
        assertEquals("Minor", Integer.valueOf(2), version.getMinor());
        assertEquals("Build", Integer.valueOf(3), version.getBuild());
        assertEquals("Revision", "4", version.getRevision());
    }

    @Test
    public void testGreaterMajor() throws Exception {
        // GIVEN
        String newerStr = "2.1.0";
        String olderStr = "1.2";
        Version older = Version.parse(olderStr);
        Version newer = Version.parse(newerStr);

        // WHEN 
        int result = older.compareTo(newer);

        // THEN
        assertEquals("Версия " + olderStr + " должна быть старше, чем " + newerStr, -1, result);
    }

    @Test
    public void testGreaterMinor() throws Exception {
        // GIVEN
        String newerStr = "1.3";
        String olderStr = "1.2.0";
        Version older = Version.parse(olderStr);
        Version newer = Version.parse(newerStr);

        // WHEN 
        int result = older.compareTo(newer);

        // THEN
        assertEquals("Версия " + olderStr + " должна быть старше, чем " + newerStr, -1, result);
    }

    @Test
    public void testGreaterBuild() throws Exception {
        // GIVEN
        String newerStr = "1.1.3";
        String olderStr = "1.1";
        Version older = Version.parse(olderStr);
        Version newer = Version.parse(newerStr);

        // WHEN 
        int result = older.compareTo(newer);

        // THEN
        assertEquals("Версия " + olderStr + " должна быть старше, чем " + newerStr, -1, result);
    }

    @Test
    public void testGreaterRevision() throws Exception {
        // GIVEN
        String newerStr = "1.2.3-beta";
        String olderStr = "1.2.3-alpha";
        Version older = Version.parse(olderStr);
        Version newer = Version.parse(newerStr);

        // WHEN 
        int result = older.compareTo(newer);

        // THEN
        assertEquals("Версия " + olderStr + " должна быть старше, чем " + newerStr, -1, result);
    }

    /**
     * Проверка сравнения версий, если у первой минорное значение больше, чем у
     * второй, а номар сборки меньше
     *
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testMinorGreaterBuildLesser() throws NugetFormatException {
        // GIVEN
        final String newerStr = "1.3.3";
        final String olderStr = "1.2.5";
        Version older = Version.parse(olderStr);
        Version newer = Version.parse(newerStr);

        // WHEN 
        int result = newer.compareTo(older);

        // THEN
        assertEquals("Версия " + newerStr + " должна быть старше, чем " + olderStr, 1, result);
    }

    /**
     * Преобразование строки в версию и обратно не должно менять строку
     *
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testVersionWithThreeDigit() throws NugetFormatException {
        //GIVEN
        final String sourceVersion = "4.0.10827";
        //WHEN
        Version version = Version.parse(sourceVersion);
        //THEN
        assertEquals("Версия не должна измениться", sourceVersion, version.toString());
    }

    /**
     * Преобразование строки в версию для неполной релизной версии
     *
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testNonReleaseVersion() throws NugetFormatException {
        //GIVEN
        final String sourceVersion = "2.5-a";
        //WHEN
        Version version = Version.parse(sourceVersion);
        //THEN
        assertEquals("Major", Integer.valueOf(2), version.getMajor());
        assertEquals("Minor", Integer.valueOf(5), version.getMinor());
        assertNull("Build", version.getBuild());
        assertEquals("Revision", "-a", version.getRevision());
    }

    /**
     * Преобразование строки в версию для неполной релизной версии
     *
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testFullNonReleaseVersion() throws NugetFormatException {
        //GIVEN
        final String sourceVersion = "3.0.0.1034-rc";
        //WHEN
        Version version = Version.parse(sourceVersion);
        //THEN
        assertEquals("Major", Integer.valueOf(3), version.getMajor());
        assertEquals("Minor", Integer.valueOf(0), version.getMinor());
        assertEquals("Build", Integer.valueOf(0), version.getBuild());
        assertEquals("Revision", "1034-rc", version.getRevision());
    }

    /**
     * Преобразование строки в версию для неполной версии
     *
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testNonReleaseVersionNoBuild() throws NugetFormatException {
        //GIVEN
        final String sourceVersion = "10.0.0-prerelease";
        //WHEN
        Version version = Version.parse(sourceVersion);
        //THEN
        assertEquals("Major", Integer.valueOf(10), version.getMajor());
        assertEquals("Minor", Integer.valueOf(0), version.getMinor());
        assertEquals("Build", Integer.valueOf(0), version.getBuild());
        assertEquals("Revision", "-prerelease", version.getRevision());
    }

    /**
     * проверка сравнений версий, младшая ревизия которых - число
     *
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testNumberVersionCompare() throws NugetFormatException {
        //GIVEN
        final Version firstVersion = Version.parse("1.1.2.3");
        final Version secondVersion = Version.parse("1.1.2.11");
        //WHEN
        final int result = firstVersion.compareTo(secondVersion);
        //THEN
        assertThat(result, is(lessThan(0)));
    }

    /**
     * проверка сравнений версий, младшая ревизия которых - строка
     *
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testNotNumberVersionCompare() throws NugetFormatException {
        //GIVEN
        final Version firstVersion = Version.parse("1.1.2.3-RC1");
        final Version secondVersion = Version.parse("1.1.2.11-RC2");
        //WHEN
        final int result = firstVersion.compareTo(secondVersion);
        //THEN
        assertThat(result, is(greaterThan(0)));
    }
}

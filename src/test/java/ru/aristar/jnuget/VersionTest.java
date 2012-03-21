package ru.aristar.jnuget;

import static junit.framework.Assert.*;
import org.junit.Test;

/**
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

    @Test
    public void testMinorGreaterBuildLesser() throws Exception {
        // GIVEN
        String newerStr = "1.3.3";
        String olderStr = "1.2.5";
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
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testVersionWithThreeDigit() throws Exception {
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
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testNonReleaseVersion() throws Exception {
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
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testFullNonReleaseVersion() throws Exception {
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
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testNonReleaseVersionNoBuild() throws Exception {
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
}

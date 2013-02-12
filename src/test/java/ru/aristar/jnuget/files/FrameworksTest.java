package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Тест получения информации о фрейморках для которых предназначен пакет
 *
 * @author sviridov
 */
public class FrameworksTest {

    /**
     *
     * @throws IOException ошибка чтения пакета с диска
     * @throws NugetFormatException некорректный формат пакета
     */
    @Test
    public void testReadFrameworkTempNupkg() throws IOException, NugetFormatException {
        //GIVEN
        InputStream inputStream = FrameworksTest.class.getResourceAsStream("/nupkg/test.package.4.1.0.0.nupkg");
        TempNupkgFile tempNupkgFile = new TempNupkgFile(inputStream);
        //WHEN
        EnumSet<Framework> result = tempNupkgFile.getTargetFramework();
        //THEN
        assertThat(result, is(hasItems(Framework.net40, Framework.sl4, Framework.sl5)));
    }

    /**
     * Проверка чтения фреймворков по умолчанию из удаленного пакетв
     *
     * @throws NugetFormatException ошибка создания пакета
     */
    @Test
    @Ignore
    public void testReadFrameworkRemoteNupkg() throws NugetFormatException {
        //GIVEN
        RemoteNupkg remoteNupkg = new RemoteNupkg(null);
        //WHEN
        EnumSet<Framework> result = remoteNupkg.getTargetFramework();
        //THEN
        assertThat(result, is(hasItems(Framework.net40)));
    }

    /**
     * Проверка извлечения списка фреймворков из строки запроса
     */
    @Test
    public void testParse() {
        //GIVEN
        String targetFramework = "net40|net40|net35|net40|net40|net40|net40|net40|net40|net40|net40|net40|net40|net40|net40";
        //WHEN
        EnumSet<Framework> result = Framework.parse(targetFramework);
        //THEN
        assertThat(result, is(hasItems(Framework.net40, Framework.net35)));
    }

    /**
     * Проверка извлечения списка фреймворков из пустой строки запроса
     */
    @Test
    public void testParseEmptyString() {
        //GIVEN
        String targetFramework = "";
        //WHEN
        EnumSet<Framework> result = Framework.parse(targetFramework);
        //THEN
        assertThat(result, is(hasItems(Framework.values())));
    }

    /**
     * Проверка извлечения списка фреймворков, разделенных плюсами
     */
    @Test
    public void testParsePlusDelimeted() {
        //GIVEN
        String targetFramework = "portable-net45+sl40+wp71+win80";
        //WHEN
        EnumSet<Framework> result = Framework.parse(targetFramework);
        //THEN
        assertThat(result, is(hasItems(Framework.net45, Framework.sl4, Framework.portable_net45, Framework.wp71)));
    }

    /**
     * Проверка получения полного набора фреймворков для net20
     */
    @Test
    public void testGetFullSet() {
        //GIVEN
        Framework framework = Framework.net20;
        //WHEN
        EnumSet<Framework> result = framework.getFullCopabilySet();
        //THEN
        Framework[] expected = {Framework.net20};
        assertArrayEquals(expected, result.toArray(new Framework[1]));
    }
}

package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.hasItems;

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
}

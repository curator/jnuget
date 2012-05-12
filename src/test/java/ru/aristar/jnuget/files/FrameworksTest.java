package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
        EnumSet<Frameworks> result = tempNupkgFile.getTargetFramework();
        //THEN
        assertThat(result, is(hasItems(Frameworks.net40, Frameworks.sl4, Frameworks.sl5)));
    }

    @Test
    public void testReadFrameworkRemoteNupkg() throws NugetFormatException {
        //GIVEN
        RemoteNupkg remoteNupkg = new RemoteNupkg(null);
        //WHEN
        EnumSet<Frameworks> result = remoteNupkg.getTargetFramework();
        //THEN
        assertThat(result, is(hasItems(Frameworks.net40)));
    }
}

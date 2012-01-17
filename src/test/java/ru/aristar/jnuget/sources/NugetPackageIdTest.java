package ru.aristar.jnuget.sources;

import junit.framework.Assert;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 *
 * @author Unlocker
 */
public class NugetPackageIdTest {
    
    @Test
    public void testParseWithFullVersion() throws Exception{
        // Given
        final String idStr = "NUnit";
        final String versionStr = "2.5.9.10348";
        final String filename = String.format("%s.%s.nupkg", idStr, versionStr);
        // When
        NugetPackageId result = NugetPackageId.parse(filename);
        // Then
        Assert.assertEquals("Неправильный id пакета", idStr, result.getId());
        Assert.assertEquals("Неправильный версия пакета", Version.parse(versionStr), result.getVersion());
    }
    
    @Test
    public void testToString() throws Exception {
        // Given
        NugetPackageId info = new NugetPackageId();
        info.setId("NUnit");
        info.setVersion(Version.parse("2.5.9.10348"));
        // When
        String result = info.toString();
        // Then
        Assert.assertEquals("Неправильное имя файла", "NUnit.2.5.9.10348.nupkg", result);
    }
            
}

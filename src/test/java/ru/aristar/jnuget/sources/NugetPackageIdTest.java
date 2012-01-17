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
    public void TestParseWithFullVersion() throws Exception{
        // Given
        final String idStr = "NUnit";
        final String versionStr = "2.5.9.10348";
        final String filename = String.format("%1$s.%2$s.nupkg", idStr, versionStr);
        // When
        NugetPackageId result = NugetPackageId.Parse(filename);
        // Then
        Assert.assertEquals("Неправильный id пакета", idStr, result.getId());
        Assert.assertEquals("Неправильный версия пакета", Version.parse(versionStr), result.getVersion());
    }
            
}

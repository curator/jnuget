package ru.aristar.jnuget.sources;

import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class RemotePackageSourceTest {

    @Test
    public void testGetLastVersionPackage() {
        RemotePackageSource packageSource = new RemotePackageSource();
        packageSource.getLastVersionPackage("id");
        fail("Тест не написан");
    }
}

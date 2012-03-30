package ru.aristar.jnuget;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class VersionTypeAdapterTest {

    @Test
    public void testUnmarshal() throws Exception {
        VersionTypeAdapter adapter = new VersionTypeAdapter();
        Version version = adapter.unmarshal("1.2.3-asdasdas");
        assertEquals("Преобразование строки в версию", new Version(1, 2, 3, "-asdasdas"), version);
    }
}

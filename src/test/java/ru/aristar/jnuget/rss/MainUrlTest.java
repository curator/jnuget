package ru.aristar.jnuget.rss;

import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class MainUrlTest {

    @Test
    public void testUnmarshallMainUrlFromXml() throws Exception {
        //GIVEN
        InputStream inputStream = MainUrlTest.class.getResourceAsStream("/main.document.xml");
        //WHEN
        MainUrl result = MainUrl.parse(inputStream);
        //THEN
        assertEquals("URL сервлета", "Полный URL, куда должен быть задеполен сервлет", result.getBaseUrl());
        assertEquals("Описание пакета", "Default", result.getTitle());
        assertEquals("Описание пакета", "Packages", result.getCollectionTitle());
        assertEquals("Описание пакета", "Packages", result.getCollectionHref());
    }
}

package ru.aristar.integration;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class RssIntegrationTests {

    /**
     * Корневой URL содержит страницу с общей информацией
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testDoGet() throws Exception {
        //GIVEN
        WebConversation webConversation = new WebConversation();
        //WHEN
        WebResponse response = webConversation.getResponse("http://localhost:8088");
        //THEN
        assertTrue(response.getText().contains("You are running JNuGet.Server"));
    }
}

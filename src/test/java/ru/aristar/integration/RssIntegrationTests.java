package ru.aristar.integration;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class RssIntegrationTests {

    /**
     * good XML input
     */
    private static final String XML = "./src/test/resources/xml/file.xml";
    /**
     * URL
     */
    private static final String URL = "http://localhost:8088";
    private PostMethodWebRequest req;
    private WebConversation wc;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        req = new PostMethodWebRequest(URL);
        wc = new WebConversation();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link biz.zenoconsulting.jersey.MyServlet#doGet()}.
     */
    @Test
    public void testDoGet() throws Exception {
        WebResponse wr = wc.getResponse(URL);
        assertTrue(wr.getText().contains("hello"));
    }
}

package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author sviridov
 */
public class NugetPrefixFilterTest {

    @Test
    public void testFilterClass() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/CustomXml/test.prefix.xml");
        InputSource inputSource = new InputSource(inputStream);
        XMLReader reader = XMLReaderFactory.createXMLReader();
        Map<String, String> uriToPrefix = new HashMap<>();
        uriToPrefix.put("element_namespace1", "m1");
        uriToPrefix.put("element_namespace2", "m2");
        uriToPrefix.put("attribute_namespace1", "m3");
        NugetPrefixFilter filter = new NugetPrefixFilter(uriToPrefix);
        filter.setParent(reader);
        SAXSource source = new SAXSource(filter, inputSource);
        //WHEN        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(source, result);
        //THEN
        System.out.println(stringWriter.toString());

        fail("Тест не реализован");
    }
}

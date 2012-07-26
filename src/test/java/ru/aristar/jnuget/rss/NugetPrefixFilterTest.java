package ru.aristar.jnuget.rss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import static org.junit.Assert.*;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import ru.aristar.jnuget.Version;

/**
 * Тест фильтра, переносящего объявление указанных префиксов пространств имен в
 * корневой элемент XMLдокумента
 *
 * @author sviridov
 */
public class NugetPrefixFilterTest {

    /**
     * Тест на искусственном XML
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testFilterClass() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/customxml/test.prefix.xml");
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
        String xmlString = stringWriter.toString();
        //THEN
        assertTrue(xmlString.contains("<m2:l/>"));
        assertTrue(xmlString.contains("<m2:k m3:val=\"value\">"));
        assertTrue(xmlString.contains("</m1:root>"));
    }

    /**
     * Тест на реальном RSS пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testClearRealRss() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/customxml/real.data.xml");
        InputSource inputSource = new InputSource(inputStream);
        XMLReader reader = XMLReaderFactory.createXMLReader();
        Map<String, String> uriToPrefix = new HashMap<>();
        uriToPrefix.put("http://www.w3.org/2005/Atom", "atom");
        uriToPrefix.put("http://schemas.microsoft.com/ado/2007/08/dataservices/metadata", "m");
        uriToPrefix.put("http://schemas.microsoft.com/ado/2007/08/dataservices/scheme", "ds");
        uriToPrefix.put("http://schemas.microsoft.com/ado/2007/08/dataservices", "d");
        NugetPrefixFilter filter = new NugetPrefixFilter(uriToPrefix);
        filter.setParent(reader);
        SAXSource source = new SAXSource(filter, inputSource);
        //WHEN        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(byteArrayOutputStream);
        transformer.transform(source, result);
        byteArrayOutputStream.flush();
        //THEN
        PackageFeed feed = PackageFeed.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        assertEquals("Количество пакетов", 1, feed.getEntries().size());
        PackageEntry entry = feed.getEntries().get(0);
        assertEquals("Content", "http://ws209.neolant.loc:8084/nuget/download/FluentAssertions/1.6.0", entry.getContent().getSrc());
        assertEquals("Число загрузок", Integer.valueOf(-1), entry.getProperties().getDownloadCount());
        assertEquals("Версия пакета", Version.parse("1.6.0"), entry.getProperties().getVersion());
    }
}

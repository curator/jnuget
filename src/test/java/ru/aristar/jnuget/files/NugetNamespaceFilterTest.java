package ru.aristar.jnuget.files;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author sviridov
 */
public class NugetNamespaceFilterTest {

    /**
     * Проверка того, что старое пространство имен будет заменено на новое
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testChangeUri() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/nuspec/NLog.nuspec.xml");
        InputSource inputSource = new InputSource(inputStream);

        XMLReader reader = XMLReaderFactory.createXMLReader();
        NugetNamespaceFilter inFilter = new NugetNamespaceFilter();
        inFilter.setParent(reader);

        //WHEN
        SAXSource source = new SAXSource(inFilter, inputSource);

        //THEN
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        DOMResult domResult = new DOMResult(documentBuilder.newDocument());
        transformer.transform(source, domResult);
        String namespace = domResult.getNode().getFirstChild().getNamespaceURI();

        assertEquals("Пространство имен", NuspecFile.NUSPEC_XML_NAMESPACE_2011, namespace);
    }
}

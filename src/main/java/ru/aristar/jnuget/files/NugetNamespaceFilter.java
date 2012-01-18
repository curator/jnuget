package ru.aristar.jnuget.files;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 *
 * @author sviridov
 */
public class NugetNamespaceFilter extends XMLFilterImpl {

    /**
     * URI, который следует заменить
     */
    private final String sourceUri;
    /**
     * URI на который следует заменить
     */
    private final String targetUri;

    /**
     * Конструктор, использующий пространства имен по умолчанию
     */
    public NugetNamespaceFilter() {
        this(NuspecFile.NUSPEC_XML_NAMESPACE_2010, NuspecFile.NUSPEC_XML_NAMESPACE_2011);
    }

    /**
     *
     * @param sourceUri URI, который следует заменить
     * @param targetUri URI на который следует заменить
     */
    public NugetNamespaceFilter(String sourceUri, String targetUri) {
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (sourceUri.equals(uri)) {
            uri = targetUri;
        }
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (sourceUri.equals(uri)) {
            uri = targetUri;
        }
        super.startElement(uri, localName, qName, atts);
    }
}

package ru.aristar.jnuget.files;

import ru.aristar.jnuget.files.nuspec.NuspecFile;
import java.util.Arrays;
import java.util.HashSet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 *
 * @author sviridov
 */
public class NugetNamespaceFilter extends XMLFilterImpl {

    /**
     * Список URI, которые следует заменить
     */
    private final HashSet<String> sourceUris = new HashSet<>();
    /**
     * URI на который следует заменить
     */
    private final String targetUri;

    /**
     * Конструктор, использующий пространства имен по умолчанию
     */
    public NugetNamespaceFilter() {
        this(new String[]{NuspecFile.NUSPEC_XML_NAMESPACE_2010, NuspecFile.NUSPEC_XML_NAMESPACE_EMPTY},
                NuspecFile.NUSPEC_XML_NAMESPACE_2011);
    }

    /**
     * @param sourceUris список URI, которые следует заменить
     * @param targetUri URI на который следует заменить
     */
    public NugetNamespaceFilter(String[] sourceUris, String targetUri) {
        this.sourceUris.addAll(Arrays.asList(sourceUris));
        this.targetUri = targetUri;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (sourceUris.contains(uri)) {
            uri = targetUri;
        }
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (sourceUris.contains(uri)) {
            uri = targetUri;
        }
        super.startElement(uri, localName, qName, atts);
    }
}

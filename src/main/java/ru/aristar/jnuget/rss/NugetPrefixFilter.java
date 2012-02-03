package ru.aristar.jnuget.rss;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Фильтр, переносящий декларацию некоторых пространств имен в корневой элемент
 * документа
 *
 * @author sviridov
 */
public class NugetPrefixFilter extends XMLFilterImpl {

    /**
     * @param uriToPrefix маппинг URI на префикс
     */
    public NugetPrefixFilter(Map<String, String> uriToPrefix) {
        this.uriToPrefix = uriToPrefix;
    }
    /**
     * маппинг URI на префикс
     */
    private final Map<String, String> uriToPrefix;
    /**
     * маппинг префикс на URI
     */
    private final Map<String, String> prefixToUri = new HashMap<>();

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        for (Entry<String, String> entry : uriToPrefix.entrySet()) {
            super.startPrefixMapping(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void endDocument() throws SAXException {
        for (Entry<String, String> entry : uriToPrefix.entrySet()) {
            super.endPrefixMapping(entry.getValue());
        }
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        qName = changeNamePrefix(uri, localName, qName);
        AttributesImpl newAttributes = new AttributesImpl();

        for (int i = 0; i < atts.getLength(); i++) {
            String aType = atts.getType(i);
            String aqName = atts.getQName(i);
            String aUri = atts.getURI(i);
            String aValue = atts.getValue(i);
            String aLocalName = atts.getLocalName(i);
            if (uriToPrefix.containsKey(aUri)) {
                aqName = uriToPrefix.get(aUri) + ":" + aLocalName;
            }
            if (!qName.startsWith("xmlns:") && !uriToPrefix.containsKey(aValue)) {
                newAttributes.addAttribute(aUri, aLocalName, aqName, aType, aValue);
            }
        }
        super.startElement(uri, localName, qName, newAttributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        qName = changeNamePrefix(uri, localName, qName);
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        prefixToUri.put(prefix, uri);
        if (!uriToPrefix.containsKey(uri)) {
            super.startPrefixMapping(prefix, uri);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        String uri = prefixToUri.get(prefix);
        if (!uriToPrefix.containsKey(uri)) {
            super.endPrefixMapping(prefix);
        }
    }

    /**
     * Заменяет префикс у имени элемента
     *
     * @param uri URI элемента
     * @param localName имя без префикса
     * @param qName имя с префиксом
     * @return имя с измененным префиксом
     */
    private String changeNamePrefix(String uri, String localName, String qName) {
        if (uri != null && uriToPrefix.containsKey(uri)) {
            qName = uriToPrefix.get(uri) + ":" + localName;
        }
        return qName;
    }
}

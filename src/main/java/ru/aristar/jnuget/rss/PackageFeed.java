package ru.aristar.jnuget.rss;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "feed", namespace = PackageFeed.ATOM_XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"title", "id", "updated", /*
     * "link",
     */ "entries"})
public class PackageFeed {

    @XmlElement(name = "title", namespace = ATOM_XML_NAMESPACE)
    private Title title = new Title("Packages");
    @XmlElement(name = "id", namespace = ATOM_XML_NAMESPACE)
    private String id;
    @XmlElement(name = "updated", type = Date.class, namespace = ATOM_XML_NAMESPACE)
    private Date updated;
    //TODO Добавить Link
    @XmlElement(name = "entry", namespace = ATOM_XML_NAMESPACE)
    private List<PackageEntry> entries;

    public List<PackageEntry> getEntries() {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        return entries;
    }

    public void setEntries(List<PackageEntry> entries) {
        this.entries = entries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title.value : null;
    }

    public void setTitle(String title) {
        this.title = new Title(title);
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    /**
     * @return представление объекта в виде XML
     * @throws JAXBException ошибка преобразования в XML
     */
    public String getXml() throws JAXBException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeXml(byteArrayOutputStream);
        return new String(byteArrayOutputStream.toByteArray());
    }

    /**
     * Записывает созержимое класса в виде XML документа в поток
     *
     * @param outputStream поток для записи
     * @throws JAXBException ошибка преобразования в XML
     */
    public void writeXml(OutputStream outputStream) throws JAXBException {
        //Первичная сереализация
        JAXBContext context = JAXBContext.newInstance(this.getClass());
        Marshaller marshaller = context.createMarshaller();
        Map<String, String> uriToPrefix = new HashMap<>();
        uriToPrefix.put("http://www.w3.org/2005/Atom", "atom");
        uriToPrefix.put("http://schemas.microsoft.com/ado/2007/08/dataservices/metadata", "m");
        uriToPrefix.put("http://schemas.microsoft.com/ado/2007/08/dataservices/scheme", "ds");
        uriToPrefix.put("http://schemas.microsoft.com/ado/2007/08/dataservices", "d");
        NugetPrefixFilter filter = new NugetPrefixFilter(uriToPrefix);
        filter.setContentHandler(new XMLSerializer(outputStream, new OutputFormat()));
        marshaller.marshal(this, filter);
    }

    public static PackageFeed parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PackageFeed.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (PackageFeed) unmarshaller.unmarshal(inputStream);
    }
    public static final String ATOM_XML_NAMESPACE = "http://www.w3.org/2005/Atom";
}

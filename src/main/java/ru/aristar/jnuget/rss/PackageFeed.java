package ru.aristar.jnuget.rss;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "feed", namespace = PackageFeed.ATOM_XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class PackageFeed {

    @XmlElement(name = "title")
    private String title = "Packages";
    @XmlElement(name = "id", namespace = ATOM_XML_NAMESPACE)
    private String id;
    @XmlElement(name = "updated", type = Date.class, namespace = ATOM_XML_NAMESPACE)
    private Date updated;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getXml() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(this.getClass());
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(this, writer);
        return writer.toString();
    }

    public static PackageFeed parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PackageFeed.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (PackageFeed) unmarshaller.unmarshal(inputStream);
    }
    public static final String ATOM_XML_NAMESPACE = "http://www.w3.org/2005/Atom";
}

package ru.aristar.jnuget;

import java.io.File;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "feed", namespace = "http://www.w3.org/2005/Atom")
class PackageFeed {

    @XmlElement(name = "title")
    private String title = "Packages";
    @XmlElement(name = "id")
    private String id;
    @XmlElement(name = "updated", type = Date.class)
    private Date updated;
    private List<PackageEntry> entries;

    public String getXml() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(this.getClass());
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(this, writer);
        return writer.toString();
    }
}

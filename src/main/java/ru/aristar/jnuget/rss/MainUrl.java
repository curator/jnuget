package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import ru.aristar.jnuget.UrlFactory;

/**
 * Xml корневого узла NuGet
 *
 * @author sviridov
 */
@XmlRootElement(name = "service", namespace = "http://www.w3.org/2007/app")
@XmlAccessorType(XmlAccessType.NONE)
public class MainUrl {

    public static class Collection {

        @XmlAttribute(name = "href")
        private String href = "Packages";
        private String title;

        @XmlElement(name = "title", namespace = "http://www.w3.org/2005/Atom")
        private String getTitle() {
            if (title == null) {
                title = "Packages";
            }
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @XmlType(propOrder = {"title", "collection"})
    public static class Workspace {

        private String title;
        @XmlElement(name = "collection", namespace = "http://www.w3.org/2007/app")
        private Collection collection = new Collection();

        @XmlElement(name = "title", namespace = "http://www.w3.org/2005/Atom")
        private String getTitle() {
            if (title == null) {
                title = "Default";
            }
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    private UrlFactory urlFactory;
    @XmlElement(name = "workspace", namespace = "http://www.w3.org/2007/app")
    private Workspace workspace = new Workspace();
    private String baseUrl;

    @XmlAttribute(name = "xml:base", namespace = "")
    public String getBaseUrl() {
        if (baseUrl == null) {
            baseUrl = "Полный URL, куда должен быть задеполен сервлет";
        }
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTitle() {
        return workspace.title;
    }

    public String getCollectionHref() {
        return workspace.collection.href;
    }

    public String getCollectionTitle() {
        return workspace.collection.title;
    }

    public MainUrl() {
    }

    public MainUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void writeXml(Writer writer) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(this.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(this, writer);
    }

    public static MainUrl parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MainUrl.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        MainUrl result = (MainUrl) unmarshaller.unmarshal(inputStream);
        return result;
    }
}

package ru.aristar.jnuget.rss;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
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
import javax.xml.bind.annotation.XmlType;
import ru.aristar.jnuget.Author;
import ru.aristar.jnuget.NugetContext;
import ru.aristar.jnuget.files.ClassicNupkg;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "entry", namespace = PackageFeed.ATOM_XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"id", "title", "summary", "updated", "author", "links",
    "category", "content", "properties"})
public class PackageEntry {

    protected NugetContext nugetContext;
    /**
     * Заголовок вложения
     */
    @XmlElement(name = "title", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private Title title;
    /**
     * Описание вложения
     */
    @XmlElement(name = "summary", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private Title summary = new Title();
    /**
     * Дата обновления
     */
    @XmlElement(name = "updated", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private Date updated;
    /**
     * Автор пакета
     */
    @XmlElement(name = "author", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private Author author;
    /**
     * Свойства пакета
     */
    @XmlElement(name = "properties", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
    private EntryProperties properties;
    @XmlElement(name = "link", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private List<Link> links;
    @XmlElement(name = "category", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private AtomElement category;
    @XmlElement(name = "content", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private AtomElement content;

    private String getCombineIdAndVersion() {
        return "(Id='" + getTitle() + "',Version='"
                + getProperties().getVersion().toString() + "')";
    }

    /**
     * Конструктор по умолчанию
     */
    public PackageEntry() {
    }

    public PackageEntry(Nupkg nupkgFile) {
        this(nupkgFile.getNuspecFile(), nupkgFile.getUpdated(), null);
    }

    public PackageEntry(Nupkg nupkgFile, NugetContext context) throws NoSuchAlgorithmException, IOException {
        this(nupkgFile.getNuspecFile(), nupkgFile.getUpdated(), context);
        this.getProperties().setPackageHash(nupkgFile.getHash().toString());
        this.getProperties().setPackageSize(nupkgFile.getSize());
        this.getProperties().setPublished(nupkgFile.getUpdated());
    }

    public PackageEntry(NuspecFile nuspecFile, Date updated, NugetContext nugetContext) {
        this.nugetContext = nugetContext;
        this.title = new Title(nuspecFile.getId());
        getProperties().setNuspec(nuspecFile);
        this.updated = updated;
        this.author = new Author(nuspecFile.getAuthors());
        this.getLinks().add(new Link("edit-media", "Package",
                "Packages" + getCombineIdAndVersion() + "/$value"));
        this.getLinks().add(new Link("edit", "Package",
                "Packages" + getCombineIdAndVersion()));
        this.category = new AtomElement();
        category.setTerm("NuGet.Server.DataServices.Package");
        category.setScheme("http://schemas.microsoft.com/ado/2007/08/dataservices/scheme");
        this.content = new AtomElement();
        content.setType("application/zip");
        content.setSrc(getRootUri() + "download/" + title.value + "/"
                + nuspecFile.getVersion());
    }

    /**
     * Идентификатор вложения
     */
    @XmlElement(name = "id", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    public String getId() {
        return getRootUri() + "nuget/Packages(Id='" + getTitle() + "',Version='"
                + getProperties().getVersion().toString() + "')";
    }

    public String getRootUri() {
        if (nugetContext == null) {
            return null;
        }
        return nugetContext.getRootUri().toString();
    }

    public String getTitle() {
        return title.value;
    }

    public void setTitle(String title) {
        this.title = new Title(title);
    }

    public String getSummary() {
        return summary.value;
    }

    public void setSummary(String summary) {
        this.summary = new Title(summary);
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public final EntryProperties getProperties() {
        if (properties == null) {
            properties = new EntryProperties();
        }
        return properties;
    }

    public List<Link> getLinks() {
        if (links == null) {
            links = new ArrayList<Link>();
        }
        return links;
    }

    public AtomElement getCategory() {
        return category;
    }

    public AtomElement getContent() {
        return content;
    }

    /**
     * Читает класс PackageEntry из потока с XML
     *
     * @param inputStream поток с XML
     * @return распознанный экземпляр PackageEntry
     * @throws JAXBException ошибка преобразования
     */
    public static PackageEntry parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PackageEntry.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (PackageEntry) unmarshaller.unmarshal(inputStream);
    }
}

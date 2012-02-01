package ru.aristar.jnuget.rss;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import ru.aristar.jnuget.Author;
import ru.aristar.jnuget.NugetContext;
import ru.aristar.jnuget.files.ClassicNupkg;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"id", "title", "summary", "updated", "author", "links",
    "category", "content", "properties"})
public class PackageEntry {

    protected NugetContext nugetContext;
    /*
     * <entry>
     * <id>http://localhost:8090/nuget/nuget/Packages(Id='NUnit',Version='2.5.9.10348')</id>
     * <title type="text">NUnit</title> <summary type="text"></summary>
     * <updated>2011-09-23T05:30:48Z</updated> <author> <name>NUnit</name>
     * </author> <link rel="edit-media" title="Package"
     * href="Packages(Id='NUnit',Version='2.5.9.10348')/$value" /> <link
     * rel="edit" title="Package"
     * href="Packages(Id='NUnit',Version='2.5.9.10348')" /> <category
     * term="NuGet.Server.DataServices.Package"
     * scheme="http://schemas.microsoft.com/ado/2007/08/dataservices/scheme" />
     * <content type="application/zip"
     * src="http://localhost:8090/nuget/download/NUnit/2.5.9.10348" />
     * <m:properties
     * xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata"
     * xmlns:d="http://schemas.microsoft.com/ado/2007/08/dataservices"> ......
     * </m:properties> </entry>
     */
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

    public PackageEntry(ClassicNupkg nupkgFile) {
        this(nupkgFile.getNuspecFile(), nupkgFile.getUpdated(), null);
    }

    public PackageEntry(ClassicNupkg nupkgFile, NugetContext context) throws NoSuchAlgorithmException, IOException {
        this(nupkgFile.getNuspecFile(), nupkgFile.getUpdated(), context);
        this.getProperties().setPackageHash(nupkgFile.getHash().toString());
        this.getProperties().setPackageSize(nupkgFile.getSize());
        this.getProperties().setPublished(nupkgFile.getUpdated());
    }

    public PackageEntry(NuspecFile nuspecFile, Date updated, NugetContext nugetContext) {
        this.nugetContext = nugetContext;
        title = new Title(nuspecFile.getId());
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
}

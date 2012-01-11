package ru.aristar.jnuget.rss;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import ru.aristar.jnuget.Author;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.NONE)
public class PackageEntry {

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
     * Идентификатор вложения
     */
    @XmlElement(name = "id", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    private String id;
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

    /**
     * Конструктор по умолчанию
     */
    public PackageEntry() {
    }

    public PackageEntry(NupkgFile nupkgFile) {
        this(nupkgFile.getNuspecFile(), nupkgFile.getUpdated());
    }

    public PackageEntry(NuspecFile nuspecFile, Date updated) {
        id = nuspecFile.getId();
        title = new Title(nuspecFile.getId());
        getProperties().setNuspec(nuspecFile);
        this.updated = updated;
        this.author = new Author(nuspecFile.getAuthors());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}

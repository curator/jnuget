package ru.aristar.jnuget.rss;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import ru.aristar.jnuget.Author;
import ru.aristar.jnuget.files.Hash;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.nuspec.NuspecFile;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "entry", namespace = PackageFeed.ATOM_XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"id", "title", "summary", "updated", "author", "links",
    "category", "content", "properties"})
public class PackageEntry {

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
    /**
     * Категория RSS вложения
     */
    private AtomElement category;
    /**
     * Указатель на содержимое (архив) пакета
     */
    private AtomElement content;
    /**
     * Файл спецификации пакета
     */
    private NuspecFile nuspecFile;

    private String getCombineIdAndVersion() {
        return "(Id='" + getTitle() + "',Version='"
                + getProperties().getVersion().toString() + "')";
    }

    /**
     * Конструктор по умолчанию
     */
    public PackageEntry() {
    }

    /**
     * @param nupkgFile файл пакета
     * @throws NoSuchAlgorithmException не установлены библиотеки подсчета Hash
     * @throws IOException ошибка чтения файла пакета
     * @throws NugetFormatException некорректная спецификация пакета
     */
    public PackageEntry(Nupkg nupkgFile) throws NoSuchAlgorithmException, IOException, NugetFormatException {
        this(nupkgFile.getNuspecFile(), nupkgFile.getHash(), nupkgFile.getSize(), nupkgFile.getUpdated());
    }

    /**
     * @param nuspecFile Спецификация пакета
     * @param packageHash HASH код пакета
     * @param packageSize размер пакета
     * @param updateDate дата обновления пакета
     */
    public PackageEntry(NuspecFile nuspecFile, Hash packageHash, Long packageSize, Date updateDate) {
        this.nuspecFile = nuspecFile;
        this.title = new Title(nuspecFile.getId());
        getProperties().setNuspec(nuspecFile);
        this.updated = updateDate;
        this.author = new Author(nuspecFile.getAuthors());
        PackageEntry.this.getLinks().add(new Link("edit-media", "Package",
                "Packages" + getCombineIdAndVersion() + "/$value"));
        PackageEntry.this.getLinks().add(new Link("edit", "Package",
                "Packages" + getCombineIdAndVersion()));
        this.getProperties().setPackageHash(packageHash.toString());
        this.getProperties().setPackageSize(packageSize);
        this.getProperties().setPublished(updateDate);
    }

    /**
     * @return идентификатор вложения
     */
    @XmlElement(name = "id", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    public String getId() {
        return getRootUri() + "nuget/Packages(Id='" + getTitle() + "',Version='"
                + getProperties().getVersion().toString() + "')";
    }

    /**
     * @return строковое значение корневого URI хранилища
     */
    protected String getRootUri() {
        return null;
    }

    /**
     * @return заголовок вложения (идентификатор пакета)
     */
    public String getTitle() {
        return title.value;
    }

    /**
     * @param title заголовок вложения (идентификатор пакета)
     */
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
            links = new ArrayList<>();
        }
        return links;
    }

    /**
     * @return категория RSS вложения
     */
    @XmlElement(name = "category", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    public AtomElement getCategory() {
        if (category == null) {
            this.category = new AtomElement();
            category.setTerm("NuGet.Server.DataServices.Package");
            category.setScheme("http://schemas.microsoft.com/ado/2007/08/dataservices/scheme");
        }
        return category;
    }

    /**
     * @param category категория RSS вложения
     */
    protected void setCategory(AtomElement category) {
        this.category = category;
    }

    /**
     * @return указатель на содержимое (архив) пакета
     */
    @XmlElement(name = "content", namespace = PackageFeed.ATOM_XML_NAMESPACE)
    public AtomElement getContent() {
        if (content == null) {
            this.content = new AtomElement();
            content.setType("application/zip");
            content.setSrc(getRootUri() + "download/" + title.value + "/"
                    + nuspecFile.getVersion());
        }
        return content;
    }

    /**
     * @param content указатель на содержимое (архив) пакета
     */
    protected void setContent(AtomElement content) {
        this.content = content;

    }

    /**
     * @param packageSourceUrl URL, по которому расположен пакет
     */
    public void setContent(String packageSourceUrl) {
        AtomElement newContent = new AtomElement();
        newContent.setType("application/zip");
        newContent.setSrc(packageSourceUrl);
        setContent(newContent);
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

package ru.aristar.jnuget.files.nuspec;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import ru.aristar.jnuget.Reference;
import ru.aristar.jnuget.StringListTypeAdapter;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.VersionTypeAdapter;
import ru.aristar.jnuget.files.FrameworkAssembly;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.NugetNamespaceFilter;
import ru.aristar.jnuget.rss.EntryProperties;
import ru.aristar.jnuget.rss.PackageEntry;

/**
 * Класс, содержащий информацию о пакете NuGet
 *
 * @author sviridov
 */
@XmlRootElement(name = "package", namespace = NuspecFile.NUSPEC_XML_NAMESPACE_2011)
@XmlAccessorType(XmlAccessType.NONE)
public class NuspecFile implements Serializable {

    /**
     * Класс, обеспечивающий валидацию ошибок в XML структуре файла NuSpec
     */
    private static class NuspecXmlValidationEventHandler implements ValidationEventHandler {

        @Override
        public boolean handleEvent(ValidationEvent event) {
            return false;
        }
    }

    /**
     * Класс содержащий метаанные пакета NuGet
     */
    public static class Metadata implements Serializable {

        /**
         * Уникальный идентификатор пакета
         */
        @XmlElement(name = "id", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String id;
        /**
         * Версия пакета
         */
        @XmlElement(name = "version", namespace = NUSPEC_XML_NAMESPACE_2011)
        @XmlJavaTypeAdapter(value = VersionTypeAdapter.class)
        private Version version;
        /**
         * Заглавие
         */
        @XmlElement(name = "title", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String title;
        /**
         * Список авторов пакета
         */
        @XmlElement(name = "authors", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String authors;
        /**
         * Список владельцев пакета
         */
        @XmlElement(name = "owners", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String owners;
        /**
         * URL лицензии
         */
        @XmlElement(name = "licenseUrl", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String licenseUrl;
        /**
         * URL проекта
         */
        @XmlElement(name = "projectUrl", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String projectUrl;
        /**
         * URL иконки
         */
        @XmlElement(name = "iconUrl", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String iconUrl;
        /**
         * Зависимости от сборок, входящих в поставку .NET
         */
        @XmlElement(name = "frameworkAssembly", namespace = NUSPEC_XML_NAMESPACE_2011)
        @XmlElementWrapper(name = "frameworkAssemblies", namespace = NUSPEC_XML_NAMESPACE_2011)
        private List<FrameworkAssembly> frameworkAssembly;
        /**
         * Требуется ли запрос лицензии
         */
        @XmlElement(name = "requireLicenseAcceptance", namespace = NUSPEC_XML_NAMESPACE_2011)
        private Boolean requireLicenseAcceptance;
        /**
         * Описание пакета
         */
        @XmlElement(name = "description", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String description;
        /**
         * Примечания к релизу
         */
        @XmlElement(name = "releaseNotes", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String releaseNotes;
        /**
         * Краткое описание
         */
        @XmlElement(name = "summary", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String summary;
        /**
         * Кому пренадлежат права на пакет
         */
        @XmlElement(name = "copyright", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String copyright;
        /**
         * Язык
         */
        @XmlElement(name = "language", namespace = NUSPEC_XML_NAMESPACE_2011)
        private String language;
        /**
         * Список меток, разделенных запятыми
         */
        @XmlElement(name = "tags", namespace = NUSPEC_XML_NAMESPACE_2011)
        @XmlJavaTypeAdapter(value = StringListTypeAdapter.class)
        private List<String> tags;
        /**
         * Список ссылок
         */
        @XmlElementWrapper(name = "references", namespace = NUSPEC_XML_NAMESPACE_2011)
        @XmlElement(name = "reference", namespace = NUSPEC_XML_NAMESPACE_2011)
        private List<Reference> references;
        /**
         * Список зависимостей
         */
        @XmlElement(name = "dependencies", namespace = NUSPEC_XML_NAMESPACE_2011)
        private Dependencies dependencies;
    }
    /**
     * Метаданные пакета
     */
    @XmlElement(name = "metadata", namespace = NUSPEC_XML_NAMESPACE_2011)
    private Metadata metadata;

    private Metadata getMetadata() {
        if (metadata == null) {
            metadata = new Metadata();
        }
        return metadata;
    }

    /**
     * @return Уникальный идентификатор пакета
     */
    public String getId() {
        return getMetadata().id;
    }

    /**
     * @return Версия пакета
     */
    public Version getVersion() {
        return getMetadata().version;
    }

    /**
     * @return Заглавие
     */
    public String getTitle() {
        return getMetadata().title;
    }

    /**
     * @return Список авторов пакета
     */
    public String getAuthors() {
        return getMetadata().authors;
    }

    /**
     * @return Список владельцев пакета
     */
    public String getOwners() {
        return getMetadata().owners;
    }

    /**
     * @return Требуется ли запрос лицензии
     */
    public boolean isRequireLicenseAcceptance() {
        if (getMetadata().requireLicenseAcceptance == null) {
            return false;
        } else {
            return getMetadata().requireLicenseAcceptance;
        }
    }

    /**
     * @return Описание пакета
     */
    public String getDescription() {
        return getMetadata().description;
    }

    /**
     * @return URL лицензии
     */
    public String getLicenseUrl() {
        return getMetadata().licenseUrl;
    }

    /**
     * @return URL проекта
     */
    public String getProjectUrl() {
        return getMetadata().projectUrl;
    }

    /**
     * @return URL иконки
     */
    public String getIconUrl() {
        return getMetadata().iconUrl;
    }

    /**
     *
     * @return примечания к релизу
     */
    public String getReleaseNotes() {
        return this.getMetadata().releaseNotes;
    }

    /**
     *
     * @param releaseNotes примечания к релизу
     */
    public void setReleaseNotes(String releaseNotes) {
        this.getMetadata().releaseNotes = releaseNotes;
    }

    /**
     * @return Краткое описание пакета
     */
    public String getSummary() {
        return getMetadata().summary;
    }

    /**
     * @return Кому пренадлежат права на пакет
     */
    public String getCopyright() {
        return getMetadata().copyright;
    }

    /**
     * @return Язык
     */
    public String getLanguage() {
        return getMetadata().language;
    }

    /**
     * @return Список меток
     */
    public List<String> getTags() {
        if (getMetadata().tags == null) {
            return new ArrayList<>();
        }
        return getMetadata().tags;
    }

    /**
     * @return Список ссылок
     */
    public List<Reference> getReferences() {
        if (getMetadata().references == null) {
            return new ArrayList<>();
        }
        return getMetadata().references;
    }

    /**
     * @return зависимости пакетов, включая те, что в группах
     */
    public List<Dependency> getDependencies() {
        if (getMetadata().dependencies == null) {
            return new ArrayList<>();
        }
        return getMetadata().dependencies.getDependencies();
    }

    /**
     *
     * @return группы зависимостей, включая корневую
     */
    public List<DependenciesGroup> getDependenciesGroups() {
        return getMetadata().dependencies.getGroups();
    }

    /**
     * @return зависимости от сборок, входящих в поставку .NET
     */
    public List<FrameworkAssembly> getFrameworkAssembly() {
        if (getMetadata().frameworkAssembly == null) {
            getMetadata().frameworkAssembly = new ArrayList<>();
        }
        return getMetadata().frameworkAssembly;
    }

    /**
     * @param frameworkAssembly зависимости от сборок, входящих в поставку .NET
     */
    public void setFrameworkAssembly(List<FrameworkAssembly> frameworkAssembly) {
        getMetadata().frameworkAssembly = frameworkAssembly;
    }

    /**
     * Сохраняет спецификацию в поток данных
     *
     * @param outputStream поток для записи
     * @throws JAXBException ошибка соъранения XML
     */
    public void saveTo(OutputStream outputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(this.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(this, outputStream);
    }

    /**
     * Восстанавливает информацию о пакете из XML
     *
     * @param inputStream XML
     * @return распознанная информация о пакете
     * @throws NugetFormatException XML не соответствует спецификации NuGet
     */
    public static NuspecFile Parse(InputStream inputStream) throws NugetFormatException {
        try {
            JAXBContext context = JAXBContext.newInstance(NuspecFile.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new NuspecXmlValidationEventHandler());
            XMLReader reader = XMLReaderFactory.createXMLReader();
            NugetNamespaceFilter inFilter = new NugetNamespaceFilter();
            inFilter.setParent(reader);
            InputSource inputSource = new InputSource(inputStream);
            SAXSource saxSource = new SAXSource(inFilter, inputSource);
            NuspecFile result = (NuspecFile) unmarshaller.unmarshal(saxSource);
            return result;
        } catch (JAXBException | SAXException e) {
            throw new NugetFormatException("Ошибка чтения спецификации пакета из XML потока", e);
        }
    }

    /**
     * Конструктор по умолчанию
     */
    public NuspecFile() {
    }

    /**
     * Конструктор для обратного преобразования из RSS
     *
     * @param entry пакет в RSS
     * @throws NugetFormatException ошибка в формате версии, или не указаны
     * идентификатор и версия
     */
    public NuspecFile(PackageEntry entry) throws NugetFormatException {
        EntryProperties properties = entry.getProperties();
        if (entry.getTitle() == null || properties.getVersion() == null) {
            throw new NugetFormatException("Идентификатор и версия пакета должны"
                    + " быть указаны: " + entry.getTitle() + ':' + properties.getVersion());
        }
        getMetadata().authors = entry.getAuthor() == null ? null : entry.getAuthor().getName();
        getMetadata().dependencies = new Dependencies();
        getMetadata().id = entry.getTitle();
        getMetadata().version = properties.getVersion();
        getMetadata().tags = properties.getTags();
        getMetadata().summary = properties.getSummary();
        getMetadata().copyright = properties.getCopyright();
        getMetadata().dependencies.dependencies = properties.getDependenciesList();
        getMetadata().description = properties.getDescription();
        getMetadata().requireLicenseAcceptance = properties.getRequireLicenseAcceptance();
        getMetadata().projectUrl = properties.getProjectUrl();
        getMetadata().iconUrl = properties.getIconUrl();
        //TODO Дописать свойства
    }
    /**
     * Пространство имен для спецификации пакета NuGet 2012
     */
    public static final String NUSPEC_XML_NAMESPACE_2012 = "http://schemas.microsoft.com/packaging/2011/10/nuspec.xsd";
    /**
     * Пространство имен для спецификации пакета NuGet 2011
     */
    public static final String NUSPEC_XML_NAMESPACE_2011 = "http://schemas.microsoft.com/packaging/2011/08/nuspec.xsd";
    /**
     * Пространство имен для спецификации пакета NuGet 2010
     */
    public static final String NUSPEC_XML_NAMESPACE_2010 = "http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd";
    /**
     * Пустое пространство имен
     */
    public static final String NUSPEC_XML_NAMESPACE_EMPTY = "";
    /**
     * Расширение файла
     */
    public static final String DEFAULT_FILE_EXTENSION = ".nuspec";
}

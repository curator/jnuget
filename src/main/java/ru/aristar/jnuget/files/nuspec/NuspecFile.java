package ru.aristar.jnuget.files.nuspec;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.*;
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

    /**
     * @return Уникальный идентификатор пакета
     */
    public String getId() {
        return metadata.id;
    }

    /**
     * @return Версия пакета
     */
    public Version getVersion() {
        return metadata.version;
    }

    /**
     * @return Заглавие
     */
    public String getTitle() {
        return metadata.title;
    }

    /**
     * @return Список авторов пакета
     */
    public String getAuthors() {
        return metadata.authors;
    }

    /**
     * @return Список владельцев пакета
     */
    public String getOwners() {
        return metadata.owners;
    }

    /**
     * @return Требуется ли запрос лицензии
     */
    public boolean isRequireLicenseAcceptance() {
        if (metadata.requireLicenseAcceptance == null) {
            return false;
        } else {
            return metadata.requireLicenseAcceptance;
        }
    }

    /**
     * @return Описание пакета
     */
    public String getDescription() {
        return metadata.description;
    }

    /**
     * @return URL лицензии
     */
    public String getLicenseUrl() {
        return metadata.licenseUrl;
    }

    /**
     * @return URL проекта
     */
    public String getProjectUrl() {
        return metadata.projectUrl;
    }

    /**
     * @return URL иконки
     */
    public String getIconUrl() {
        return metadata.iconUrl;
    }

    /**
     *
     * @return примечания к релизу
     */
    public String getReleaseNotes() {
        return this.metadata.releaseNotes;
    }

    /**
     *
     * @param releaseNotes примечания к релизу
     */
    public void setReleaseNotes(String releaseNotes) {
        this.metadata.releaseNotes = releaseNotes;
    }

    /**
     * @return Краткое описание пакета
     */
    public String getSummary() {
        return metadata.summary;
    }

    /**
     * @return Кому пренадлежат права на пакет
     */
    public String getCopyright() {
        return metadata.copyright;
    }

    /**
     * @return Язык
     */
    public String getLanguage() {
        return metadata.language;
    }

    /**
     * @return Список меток
     */
    public List<String> getTags() {
        if (metadata.tags == null) {
            return new ArrayList<>();
        }
        return metadata.tags;
    }

    /**
     * @return Список ссылок
     */
    public List<Reference> getReferences() {
        if (metadata.references == null) {
            return new ArrayList<>();
        }
        return metadata.references;
    }

    /**
     * @return зависимости пакетов, включая те, что в группах
     */
    public List<Dependency> getDependencies() {
        if (metadata.dependencies == null) {
            return new ArrayList<>();
        }
        return metadata.dependencies.getDependencies();
    }

    /**
     *
     * @return группы зависимостей, включая корневую
     */
    public List<DependenciesGroup> getDependenciesGroups() {
        return metadata.dependencies.getGroups();
    }

    /**
     * @return зависимости от сборок, входящих в поставку .NET
     */
    public List<FrameworkAssembly> getFrameworkAssembly() {
        if (metadata.frameworkAssembly == null) {
            metadata.frameworkAssembly = new ArrayList<>();
        }
        return metadata.frameworkAssembly;
    }

    /**
     * @param frameworkAssembly зависимости от сборок, входящих в поставку .NET
     */
    public void setFrameworkAssembly(List<FrameworkAssembly> frameworkAssembly) {
        metadata.frameworkAssembly = frameworkAssembly;
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
        metadata = new Metadata();
        metadata.dependencies = new Dependencies();
        metadata.id = entry.getTitle();
        metadata.version = properties.getVersion();
        metadata.tags = properties.getTags();
        metadata.summary = properties.getSummary();
        metadata.copyright = properties.getCopyright();
        metadata.dependencies.dependencies = properties.getDependenciesList();
        metadata.description = properties.getDescription();
        metadata.requireLicenseAcceptance = properties.getRequireLicenseAcceptance();
        metadata.projectUrl = properties.getProjectUrl();
        metadata.iconUrl = properties.getIconUrl();
        //TODO Дописать свойства
    }
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

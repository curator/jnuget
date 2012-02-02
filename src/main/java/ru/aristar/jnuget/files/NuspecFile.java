package ru.aristar.jnuget.files;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import ru.aristar.jnuget.*;

/**
 * Класс, содержащий информацию о пакете NuGet
 *
 * @author sviridov
 */
@XmlRootElement(name = "package", namespace = NuspecFile.NUSPEC_XML_NAMESPACE_2011)
public class NuspecFile {

    /**
     * Класс содержащий метаанные пакета NuGet
     */
    public static class Metadata {

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
        @XmlElementWrapper(name = "dependencies", namespace = NUSPEC_XML_NAMESPACE_2011)
        @XmlElement(name = "dependency", namespace = NUSPEC_XML_NAMESPACE_2011)
        private List<Dependency> dependencies;
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
     * @return Список зависимостей
     */
    public List<Dependency> getDependencies() {
        if (metadata.dependencies == null) {
            return new ArrayList<>();
        }
        return metadata.dependencies;
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

    //TODO Добавить проверку схемы
    /**
     * Восстанавливает информацию о пакете из XML
     *
     * @param data XML
     * @return распознанная информация о пакете
     * @throws JAXBException ошибка преобразования XML
     * @throws SAXException ошибка фильтра SAX
     */
    public static NuspecFile Parse(byte[] data) throws JAXBException, SAXException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return Parse(inputStream);
    }

    //TODO Добавить проверку схемы
    /**
     * Восстанавливает информацию о пакете из XML
     *
     * @param inputStream XML
     * @return распознанная информация о пакете
     * @throws JAXBException ошибка преобразования XML
     * @throws SAXException ошибка фильтра SAX
     */
    public static NuspecFile Parse(InputStream inputStream) throws JAXBException, SAXException {
        JAXBContext context = JAXBContext.newInstance(NuspecFile.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        XMLReader reader = XMLReaderFactory.createXMLReader();
        NugetNamespaceFilter inFilter = new NugetNamespaceFilter();
        inFilter.setParent(reader);
        InputSource inputSource = new InputSource(inputStream);
        SAXSource saxSource = new SAXSource(inFilter, inputSource);
        NuspecFile result = (NuspecFile) unmarshaller.unmarshal(saxSource);
        return result;
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
     * Расширение файла
     */
    public static final String DEFAULT_FILE_EXTENSION = ".nuspec";
}

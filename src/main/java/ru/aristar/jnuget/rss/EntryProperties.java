package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.aristar.jnuget.StringListTypeAdapter;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NuspecFile;

/**
 * Свойства пакета, в RSS
 *
 * @author sviridov
 */
@XmlRootElement(name = "properties", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
@XmlAccessorType(XmlAccessType.NONE)
public class EntryProperties {

    /**
     * Создает XML элемент формата Microsoft DataTable из версии
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, Version value) throws ParserConfigurationException {
        String stringValue = value == null ? null : value.toString();
        return createMicrosoftElement(name, nullable, MicrosoftTypes.String, stringValue);
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из строки
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, String value) throws ParserConfigurationException {
        return createMicrosoftElement(name, nullable, MicrosoftTypes.String, value);
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из целого числа
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, Integer value) throws ParserConfigurationException {
        String stringValue = value == null ? null : value.toString();
        return createMicrosoftElement(name, nullable, MicrosoftTypes.Int32, stringValue);
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из long
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, Long value) throws ParserConfigurationException {
        String stringValue = value == null ? null : value.toString();
        return createMicrosoftElement(name, nullable, MicrosoftTypes.Int64, stringValue);
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из числа с плавающей
     * точкой
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, Double value) throws ParserConfigurationException {
        String stringValue = value == null ? null : value.toString();
        return createMicrosoftElement(name, nullable, MicrosoftTypes.Double, stringValue);
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из boolean
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, Boolean value) throws ParserConfigurationException {
        String stringValue = value == null ? null : value.toString();
        return createMicrosoftElement(name, nullable, MicrosoftTypes.Boolean, stringValue);
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из даты/времени
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, Date value) throws ParserConfigurationException {
        String stringValue = null;
        if (value != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(value);
            stringValue = javax.xml.bind.DatatypeConverter.printDateTime(calendar);
        }
        return createMicrosoftElement(name, nullable, MicrosoftTypes.DateTime, stringValue);
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из списка строк
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, List<String> value) throws ParserConfigurationException {
        String stringValue = null;
        if (value != null) {
            try {
                StringListTypeAdapter adapter = new StringListTypeAdapter();
                stringValue = adapter.marshal(value);
            } catch (Exception e) {
                throw new ParserConfigurationException("Ошибка преобразования списка строк");
            }
        }
        Element element = createMicrosoftElement(name, nullable, MicrosoftTypes.DateTime, stringValue);
        element.setAttributeNS(XMLConstants.XML_NS_URI, "space", "preserve");
        return element;
    }

    /**
     * Создает XML элемент формата Microsoft DataTable из строки
     *
     * @param name название элемента XML
     * @param nullable может ли элемент принимать значение NULL
     * @param type тип элемента по версии Microsoft
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, MicrosoftTypes type, String value) throws ParserConfigurationException {
        Document document = createDocument();
        Element element = document.createElementNS("http://schemas.microsoft.com/ado/2007/08/dataservices", name);
        if (nullable) {
            element.setAttributeNS("http://schemas.microsoft.com/ado/2007/08/dataservices/metadata", "null", Boolean.toString(nullable));
        }
        if (type != MicrosoftTypes.String) {
            element.setAttributeNS("http://schemas.microsoft.com/ado/2007/08/dataservices/metadata", "type", type.toString());
        }
        element.setTextContent(value);
        document.appendChild(element);
        document.normalizeDocument();
        return document.getDocumentElement();
    }

    /**
     * Создает документ XMl для дальнейшего использования в преобразовании
     *
     * @return пустой документ XML
     * @throws ParserConfigurationException
     */
    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder docb = dbf.newDocumentBuilder();
        Document document = docb.newDocument();
        return document;
    }

    /**
     * Возвращает список свойст, сериализованный в XML
     *
     * @return списох элементов XML
     * @throws ParserConfigurationException
     */
    @XmlAnyElement
    public List<Element> getProperties() throws ParserConfigurationException {
        ArrayList<Element> elements = new ArrayList<Element>();
        elements.add(createMicrosoftElement("Version", false, this.version));
        elements.add(createMicrosoftElement("Title", true, this.title));
        elements.add(createMicrosoftElement("IconUrl", true, this.iconUrl));
        elements.add(createMicrosoftElement("LicenseUrl", true, this.licenseUrl));
        elements.add(createMicrosoftElement("ProjectUrl", true, this.projectUrl));
        elements.add(createMicrosoftElement("ReportAbuseUrl", true, this.reportAbuseUrl));
        elements.add(createMicrosoftElement("DownloadCount", false, this.downloadCount));
        elements.add(createMicrosoftElement("VersionDownloadCount", false, this.versionDownloadCount));
        elements.add(createMicrosoftElement("RatingsCount", false, this.ratingsCount));
        elements.add(createMicrosoftElement("VersionRatingsCount", false, this.versionRatingsCount));
        elements.add(createMicrosoftElement("Rating", false, this.rating));
        elements.add(createMicrosoftElement("VersionRating", false, this.versionRating));
        elements.add(createMicrosoftElement("RequireLicenseAcceptance", false, this.requireLicenseAcceptance));
        elements.add(createMicrosoftElement("Description", false, this.description));
        elements.add(createMicrosoftElement("ReleaseNotes", true, this.releaseNotes));
        elements.add(createMicrosoftElement("Language", true, this.language));
        elements.add(createMicrosoftElement("Published", false, this.published));
        elements.add(createMicrosoftElement("Price", false, this.price));
        elements.add(createMicrosoftElement("Dependencies", false, this.dependencies));
        elements.add(createMicrosoftElement("PackageHash", false, this.packageHash));
        elements.add(createMicrosoftElement("PackageSize", false, this.packageSize));
        elements.add(createMicrosoftElement("ExternalPackageUri", true, this.externalPackageUri));
        elements.add(createMicrosoftElement("Categories", true, this.categories));
        elements.add(createMicrosoftElement("Copyright", true, this.copyright));
        elements.add(createMicrosoftElement("PackageType", true, this.packageType));
        elements.add(createMicrosoftElement("Tags", false, this.tags));
        elements.add(createMicrosoftElement("IsLatestVersion", false, this.isLatestVersion));
        elements.add(createMicrosoftElement("Summary", true, this.summary));
        return elements;
    }

    /**
     * Восстанавливает свойства класса из спискаэлементов XML
     *
     * @param properties список элементов XML
     * @throws Exception
     */
    public void setProperties(List<Element> properties) throws Exception {
        HashMap<String, Element> hashMap = new HashMap<>();
        for (Element element : properties) {
            hashMap.put(element.getLocalName(), element);
        }
        StringListTypeAdapter adapter = new StringListTypeAdapter();

        this.version = Version.parse(hashMap.get("Version").getTextContent());
        this.title = hashMap.get("Title").getTextContent();
        this.iconUrl = hashMap.get("IconUrl").getTextContent();
        this.licenseUrl = hashMap.get("LicenseUrl").getTextContent();
        this.projectUrl = hashMap.get("ProjectUrl").getTextContent();
        this.reportAbuseUrl = hashMap.get("ReportAbuseUrl").getTextContent();
        this.downloadCount = Integer.decode(hashMap.get("DownloadCount").getTextContent());
        this.versionDownloadCount = Integer.decode(hashMap.get("VersionDownloadCount").getTextContent());
        this.ratingsCount = Integer.decode(hashMap.get("RatingsCount").getTextContent());
        this.versionRatingsCount = Integer.decode(hashMap.get("VersionRatingsCount").getTextContent());
        this.rating = Double.parseDouble(hashMap.get("Rating").getTextContent());
        this.versionRating = Double.parseDouble(hashMap.get("VersionRating").getTextContent());
        this.requireLicenseAcceptance = Boolean.parseBoolean(hashMap.get("RequireLicenseAcceptance").getTextContent());
        this.description = hashMap.get("Description").getTextContent();
        this.releaseNotes = hashMap.get("ReleaseNotes").getTextContent();
        this.language = hashMap.get("Language").getTextContent();
        this.published = javax.xml.bind.DatatypeConverter.parseDateTime(hashMap.get("Published").getTextContent()).getTime();
        this.price = Double.parseDouble(hashMap.get("Price").getTextContent());
        this.dependencies = hashMap.get("Dependencies").getTextContent();
        this.packageHash = hashMap.get("PackageHash").getTextContent();
        this.packageSize = Long.decode(hashMap.get("PackageSize").getTextContent());
        this.externalPackageUri = hashMap.get("ExternalPackageUri").getTextContent();
        this.categories = hashMap.get("Categories").getTextContent();
        this.copyright = hashMap.get("Copyright").getTextContent();
        this.packageType = hashMap.get("PackageType").getTextContent();
        this.tags = adapter.unmarshal(hashMap.get("Tags").getTextContent());
        this.isLatestVersion = Boolean.parseBoolean(hashMap.get("IsLatestVersion").getTextContent());
        this.summary = hashMap.get("Summary") == null ? null : hashMap.get("Summary").getTextContent();
    }
    /**
     * Версия пакета
     */
    private Version version;
    /**
     * Заголовок пакета
     */
    private String title;
    /**
     * URL иконки
     */
    private String iconUrl;
    /**
     * URL лицензии
     */
    private String licenseUrl;
    /**
     * URL проекта
     */
    private String projectUrl;
    /**
     * URL отчета
     */
    private String reportAbuseUrl;
    /**
     * Число загрузок пакета
     */
    private Integer downloadCount;
    /**
     * Число загрузок версии пакета
     */
    private Integer versionDownloadCount;
    /**
     * Число человек, указавших рейтнг пакета
     */
    private Integer ratingsCount;
    /**
     * Число человек, указавших рейтинг версии
     */
    private Integer versionRatingsCount;
    /**
     * Рейтинг пакета
     */
    private Double rating;
    /**
     * Рейтинг версии
     */
    private Double versionRating;
    /**
     * Требуется подтверждение лицензии
     */
    private Boolean requireLicenseAcceptance;
    /**
     * Описание проекта
     */
    private String description;
    /**
     * ЗАмечания к релизу
     */
    private String releaseNotes;
    /**
     * Язык
     */
    private String language;
    /**
     * Дата публиуации
     */
    private Date published;
    /**
     * Стоимость пакета
     */
    private Double price;
    /**
     * Зависимости пакета
     */
    private String dependencies;
    /**
     * Хеш пакета
     */
    private String packageHash;
    /**
     * Размер пакета
     */
    private Long packageSize;
    /**
     * Внешний URl пакета
     */
    private String externalPackageUri;
    /**
     * Категория пакета
     */
    private String categories;
    /**
     * Права на пакет
     */
    private String copyright;
    /**
     * Тип пакета
     */
    private String packageType;
    /**
     * Теги пакета
     */
    private List<String> tags;
    /**
     * Версия является последней
     */
    private Boolean isLatestVersion;
    /**
     * Общее описание
     */
    private String summary;

    public void setNuspec(NuspecFile nuspecFile) {
        this.version = nuspecFile.getVersion();
        this.title = nuspecFile.getTitle() == null ? "" : nuspecFile.getTitle();
        this.iconUrl = "";
        this.licenseUrl = "";
        this.projectUrl = "";
        this.reportAbuseUrl = "";
        this.requireLicenseAcceptance = nuspecFile.isRequireLicenseAcceptance();
        this.description = nuspecFile.getDescription();
        this.releaseNotes = "";
        this.language = "";
        this.price = Double.valueOf(0);
        //TODO сделать зависимости
        this.dependencies = "";
        this.externalPackageUri = "";
        this.categories = "";
        this.copyright = nuspecFile.getCopyright();
        this.packageType = "";
        this.tags = nuspecFile.getTags();
        this.summary = nuspecFile.getSummary();
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getReportAbuseUrl() {
        return reportAbuseUrl;
    }

    public void setReportAbuseUrl(String reportAbuseUrl) {
        this.reportAbuseUrl = reportAbuseUrl;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        int cnt = downloadCount != null ? downloadCount : -1;
        this.downloadCount = cnt;
    }

    public Integer getVersionDownloadCount() {
        return versionDownloadCount;
    }

    public void setVersionDownloadCount(Integer versionDownloadCount) {
        int cnt = versionDownloadCount != null ? versionDownloadCount : -1;
        this.versionDownloadCount = cnt;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Integer ratingsCount) {
        int cnt = ratingsCount != null ? ratingsCount : 0;
        this.ratingsCount = cnt;
    }

    public Integer getVersionRatingsCount() {
        return versionRatingsCount;
    }

    public void setVersionRatingsCount(Integer versionRatingsCount) {
        int cnt = versionRatingsCount != null ? versionRatingsCount : -1;
        this.versionRatingsCount = cnt;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        double cnt = rating != null ? rating : -1;
        this.rating = cnt;
    }

    public Double getVersionRating() {
        return versionRating;
    }

    public void setVersionRating(Double versionRating) {
        double cnt = versionRating != null ? versionRating : -1;
        this.versionRating = cnt;
    }

    public Boolean getRequireLicenseAcceptance() {
        return requireLicenseAcceptance;
    }

    public void setRequireLicenseAcceptance(Boolean requireLicenseAcceptance) {
        boolean b = requireLicenseAcceptance == null ? false : requireLicenseAcceptance;
        this.requireLicenseAcceptance = b;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getPackageHash() {
        return packageHash;
    }

    public void setPackageHash(String packageHash) {
        this.packageHash = packageHash;
    }

    public Long getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(Long packageSize) {
        this.packageSize = packageSize;
    }

    public String getExternalPackageUri() {
        return externalPackageUri;
    }

    public void setExternalPackageUri(String externalPackageUri) {
        this.externalPackageUri = externalPackageUri;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<String>();
        }
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getIsLatestVersion() {
        return isLatestVersion;
    }

    public void setIsLatestVersion(Boolean isLatestVersion) {
        boolean b = isLatestVersion == null ? false : isLatestVersion;
        this.isLatestVersion = b;
    }

    public String getSummary() {
        if (summary == null) {
            summary = "";
        }
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static EntryProperties parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(EntryProperties.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (EntryProperties) unmarshaller.unmarshal(inputStream);
    }
}

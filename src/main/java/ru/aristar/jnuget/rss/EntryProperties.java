package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

        //***********************************************
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

        //***********************************************
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
    //*****************************************************************
    /**
     * Версия является последней
     */
    private Boolean isLatestVersion;
    /**
     * Общее описание
     */
    private String summary;
    //             <d:RatingsCount m:type="Edm.Int32">0</d:RatingsCount>
    //             <d:VersionRatingsCount m:type="Edm.Int32">-1</d:VersionRatingsCount>
    //         <m:properties xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata" 
    //                       xmlns:d="http://schemas.microsoft.com/ado/2007/08/dataservices">
    //             <d:Version>2.5.9.10348</d:Version>
    //             <d:Title m:null="true"></d:Title>
    //             <d:IconUrl m:null="true"></d:IconUrl>
    //             <d:LicenseUrl m:null="true"></d:LicenseUrl>
    //             <d:ProjectUrl m:null="true"></d:ProjectUrl>
    //             <d:ReportAbuseUrl m:null="true"></d:ReportAbuseUrl>
    //             <d:DownloadCount m:type="Edm.Int32">-1</d:DownloadCount>
    //             <d:VersionDownloadCount m:type="Edm.Int32">-1</d:VersionDownloadCount>
    //             <d:RatingsCount m:type="Edm.Int32">0</d:RatingsCount>
    //             <d:VersionRatingsCount m:type="Edm.Int32">-1</d:VersionRatingsCount>
    //             <d:Rating m:type="Edm.Double">-1</d:Rating>
    //             <d:VersionRating m:type="Edm.Double">-1</d:VersionRating>
    //             <d:RequireLicenseAcceptance m:type="Edm.Boolean">false</d:RequireLicenseAcceptance>
    //             <d:Description>Пакет модульного тестирования</d:Description>
    //             <d:ReleaseNotes m:null="true"></d:ReleaseNotes>
    //             <d:Language m:null="true"></d:Language>
    //             <d:Published m:type="Edm.DateTime">2011-09-23T05:18:55.5327281Z</d:Published>
    //             <d:Price m:type="Edm.Decimal">0</d:Price>
    //             <d:Dependencies></d:Dependencies>
    //             <d:PackageHash>CoknSJBGJ7kao2P6y9E9BuL1IkhP5LLhZ+ImtsgdxzFDpjs0QtRVOV8kxysakJu3cvw5O0hImcnVloCaQ9+Nmg==</d:PackageHash>
    //             <d:PackageSize m:type="Edm.Int64">214905</d:PackageSize>
    //             <d:ExternalPackageUri m:null="true"></d:ExternalPackageUri>
    //             <d:Categories m:null="true"></d:Categories>
    //             <d:Copyright m:null="true"></d:Copyright>
    //             <d:PackageType m:null="true"></d:PackageType>
    //             <d:Tags xml:space="preserve"> Unit test </d:Tags>
    //             <d:IsLatestVersion m:type="Edm.Boolean">true</d:IsLatestVersion>
    //             <d:Summary m:null="true"></d:Summary>
    //         </m:properties>

    public void setNuspec(NuspecFile nuspecFile) {
        this.version = nuspecFile.getVersion();
        this.description = nuspecFile.getDescription();
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

    //***************************************************
    public Boolean getIsLatestVersion() {
        return isLatestVersion;
    }

    public void setIsLatestVersion(Boolean isLatestVersion) {
        boolean b = isLatestVersion == null ? false : isLatestVersion;
        this.isLatestVersion = b;
    }

    public String getSummary() {
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

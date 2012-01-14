package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @param type тип элемента по версии Microsoft
     * @param value значение элемента
     * @return элемент XML
     * @throws ParserConfigurationException
     */
    private Element createMicrosoftElement(String name, boolean nullable, MicrosoftTypes type, Version value) throws ParserConfigurationException {
        String stringValue = value == null ? null : value.toString();
        return createMicrosoftElement(name, nullable, type, stringValue);
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
        elements.add(createMicrosoftElement("Version", false, MicrosoftTypes.String, this.version));
        elements.add(createMicrosoftElement("Title", true, MicrosoftTypes.String, this.title));
        elements.add(createMicrosoftElement("IconUrl", true, MicrosoftTypes.String, this.iconUrl));
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
    @XmlElement(name = "LicenseUrl", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement licenseUrl;
    @XmlElement(name = "ProjectUrl", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement projectUrl;
    @XmlElement(name = "ReportAbuseUrl", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement reportAbuseUrl;
    @XmlElement(name = "DownloadCount", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement downloadCount;
    @XmlElement(name = "VersionDownloadCount", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement versionDownloadCount;
    @XmlElement(name = "RatingsCount", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement ratingsCount;
    @XmlElement(name = "VersionRatingsCount", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement versionRatingsCount;
    @XmlElement(name = "Rating", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement rating;
    @XmlElement(name = "VersionRating", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement versionRating;
    @XmlElement(name = "RequireLicenseAcceptance", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement requireLicenseAcceptance;
    @XmlElement(name = "Description", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private String description;
    @XmlElement(name = "ReleaseNotes", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement releaseNotes;
    //*****************************************************************
    @XmlElement(name = "IsLatestVersion", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement isLatestVersion;
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

    public MicrosoftDatasetElement getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = new MicrosoftDatasetElement(Boolean.TRUE, null, licenseUrl);
    }

    public MicrosoftDatasetElement getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = new MicrosoftDatasetElement(Boolean.TRUE, null, projectUrl);
    }

    public MicrosoftDatasetElement getReportAbuseUrl() {
        return reportAbuseUrl;
    }

    public void setReportAbuseUrl(String reportAbuseUrl) {
        this.reportAbuseUrl = new MicrosoftDatasetElement(Boolean.TRUE, null, reportAbuseUrl);
    }

    public MicrosoftDatasetElement getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        int cnt = downloadCount != null ? downloadCount : -1;
        this.downloadCount = new MicrosoftDatasetElement(null, MicrosoftTypes.Int32, Integer.toString(cnt));
    }

    public MicrosoftDatasetElement getVersionDownloadCount() {
        return versionDownloadCount;
    }

    public void setVersionDownloadCount(Integer versionDownloadCount) {
        int cnt = versionDownloadCount != null ? versionDownloadCount : -1;
        this.versionDownloadCount = new MicrosoftDatasetElement(null, MicrosoftTypes.Int32, Integer.toString(cnt));
    }

    public MicrosoftDatasetElement getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Integer ratingsCount) {
        int cnt = ratingsCount != null ? ratingsCount : -1;
        this.ratingsCount = new MicrosoftDatasetElement(null, MicrosoftTypes.Int32, Integer.toString(cnt));
    }

    public MicrosoftDatasetElement getVersionRatingsCount() {
        return versionRatingsCount;
    }

    public void setVersionRatingsCount(Integer versionRatingsCount) {
        int cnt = versionRatingsCount != null ? versionRatingsCount : -1;
        this.versionRatingsCount = new MicrosoftDatasetElement(null, MicrosoftTypes.Int32, Integer.toString(cnt));
    }

    public MicrosoftDatasetElement getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        double cnt = rating != null ? rating : -1;
        this.rating = new MicrosoftDatasetElement(null, MicrosoftTypes.Double, Double.toString(cnt));
    }

    public MicrosoftDatasetElement getVersionRating() {
        return versionRating;
    }

    public void setVersionRating(Double versionRating) {
        double cnt = versionRating != null ? versionRating : -1;
        this.versionRating = new MicrosoftDatasetElement(null, MicrosoftTypes.Double, Double.toString(cnt));
    }

    public MicrosoftDatasetElement getRequireLicenseAcceptance() {
        return requireLicenseAcceptance;
    }

    public void setRequireLicenseAcceptance(Boolean requireLicenseAcceptance) {
        boolean b = requireLicenseAcceptance == null ? false : requireLicenseAcceptance;
        this.requireLicenseAcceptance = new MicrosoftDatasetElement(null, MicrosoftTypes.Boolean, Boolean.toString(b));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MicrosoftDatasetElement getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = new MicrosoftDatasetElement(Boolean.TRUE, null, releaseNotes);
    }

    public MicrosoftDatasetElement getIsLatestVersion() {
        return isLatestVersion;
    }

    public void setIsLatestVersion(Boolean isLatestVersion) {
        boolean b = isLatestVersion == null ? false : isLatestVersion;
        this.isLatestVersion = new MicrosoftDatasetElement(null, MicrosoftTypes.Boolean, Boolean.toString(b));
    }

    public static EntryProperties parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(EntryProperties.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (EntryProperties) unmarshaller.unmarshal(inputStream);
    }
}

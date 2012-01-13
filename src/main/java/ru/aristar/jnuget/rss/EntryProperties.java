package ru.aristar.jnuget.rss;

import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.VersionTypeAdapter;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author sviridov
 */
@XmlRootElement(name = "properties", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
@XmlAccessorType(XmlAccessType.NONE)
public class EntryProperties {

    @XmlElement(name = "Version", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    @XmlJavaTypeAdapter(value = VersionTypeAdapter.class)
    private Version version;
    @XmlElement(name = "Title", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement title;
    @XmlElement(name = "IconUrl", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement iconUrl;
    @XmlElement(name = "LicenseUrl", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement licenseUrl;
    @XmlElement(name = "ProjectUrl", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement projectUrl;
    @XmlElement(name = "ReportAbuseUrl", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement reportAbuseUrl;
    @XmlElement(name = "DownloadCount", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
    private MicrosoftDatasetElement downloadCount;

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
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public MicrosoftDatasetElement getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = new MicrosoftDatasetElement(Boolean.TRUE, null, title);
    }

    public MicrosoftDatasetElement getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = new MicrosoftDatasetElement(Boolean.TRUE, null, iconUrl);
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

    public static EntryProperties parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(EntryProperties.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (EntryProperties) unmarshaller.unmarshal(inputStream);
    }
}

package ru.aristar.jnuget.rss;

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
@XmlRootElement(name = "properties",namespace="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
@XmlAccessorType(XmlAccessType.NONE)
public class EntryProperties {
    
    @XmlElement(name="Version",namespace="http://schemas.microsoft.com/ado/2007/08/dataservices")
    @XmlJavaTypeAdapter(value=VersionTypeAdapter.class)
    private Version version;
    /*
        <m:properties xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata" 
                      xmlns:d="http://schemas.microsoft.com/ado/2007/08/dataservices">
            <d:Version>2.5.9.10348</d:Version>
            <d:Title m:null="true"></d:Title>
            <d:IconUrl m:null="true"></d:IconUrl>
            <d:LicenseUrl m:null="true"></d:LicenseUrl>
            <d:ProjectUrl m:null="true"></d:ProjectUrl>
            <d:ReportAbuseUrl m:null="true"></d:ReportAbuseUrl>
            <d:DownloadCount m:type="Edm.Int32">-1</d:DownloadCount>
            <d:VersionDownloadCount m:type="Edm.Int32">-1</d:VersionDownloadCount>
            <d:RatingsCount m:type="Edm.Int32">0</d:RatingsCount>
            <d:VersionRatingsCount m:type="Edm.Int32">-1</d:VersionRatingsCount>
            <d:Rating m:type="Edm.Double">-1</d:Rating>
            <d:VersionRating m:type="Edm.Double">-1</d:VersionRating>
            <d:RequireLicenseAcceptance m:type="Edm.Boolean">false</d:RequireLicenseAcceptance>
            <d:Description>Пакет модульного тестирования</d:Description>
            <d:ReleaseNotes m:null="true"></d:ReleaseNotes>
            <d:Language m:null="true"></d:Language>
            <d:Published m:type="Edm.DateTime">2011-09-23T05:18:55.5327281Z</d:Published>
            <d:Price m:type="Edm.Decimal">0</d:Price>
            <d:Dependencies></d:Dependencies>
            <d:PackageHash>CoknSJBGJ7kao2P6y9E9BuL1IkhP5LLhZ+ImtsgdxzFDpjs0QtRVOV8kxysakJu3cvw5O0hImcnVloCaQ9+Nmg==</d:PackageHash>
            <d:PackageSize m:type="Edm.Int64">214905</d:PackageSize>
            <d:ExternalPackageUri m:null="true"></d:ExternalPackageUri>
            <d:Categories m:null="true"></d:Categories>
            <d:Copyright m:null="true"></d:Copyright>
            <d:PackageType m:null="true"></d:PackageType>
            <d:Tags xml:space="preserve"> Unit test </d:Tags>
            <d:IsLatestVersion m:type="Edm.Boolean">true</d:IsLatestVersion>
            <d:Summary m:null="true"></d:Summary>
        </m:properties>
     */
    
    public void setNuspec(NuspecFile nuspecFile) {
        this.version = nuspecFile.getVersion();
    }
}

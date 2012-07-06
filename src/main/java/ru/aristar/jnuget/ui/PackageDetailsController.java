package ru.aristar.jnuget.ui;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.nuspec.NuspecFile;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "packageDetails")
@RequestScoped
public class PackageDetailsController {

    private int storageId;
    private String packageId;
    private Version packageVersion;
    private PackageSource packageSource;
    private Nupkg nupkg;
    private NuspecFile nuspec;

    public void init() throws NugetFormatException {
        packageSource = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        nupkg = packageSource.getPackage(packageId, packageVersion);
        nuspec = nupkg.getNuspecFile();
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageVersion() {
        return packageVersion == null ? null : packageVersion.toString();
    }

    public void setPackageVersion(String packageVersion) throws NugetFormatException {
        this.packageVersion = Version.parse(packageVersion);
    }

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public String getDescription() {
        return nuspec.getDescription();
    }
}

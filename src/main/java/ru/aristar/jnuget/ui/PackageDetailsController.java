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
 * Контроллер подробной информации о пакете
 *
 * @author sviridov
 */
@ManagedBean(name = "packageDetails")
@RequestScoped
public class PackageDetailsController {

    /**
     * Идентификатор хранилища, в котором находится пакет
     */
    private int storageId;
    /**
     * Идентификатор пакета
     */
    private String packageId;
    /**
     * Версия пакета
     */
    private Version packageVersion;
    /**
     * Файл пакета
     */
    private Nupkg nupkg;
    /**
     * Спецификация пакета
     */
    private NuspecFile nuspec;

    /**
     * Инициализация контроллера
     *
     * @throws NugetFormatException ошибка чтения спецификации пакета
     */
    public void init() throws NugetFormatException {
        PackageSource packageSource = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        nupkg = packageSource.getPackage(packageId, packageVersion);
        nuspec = nupkg.getNuspecFile();
    }

    /**
     * @return идентификатор пакета
     */
    public String getPackageId() {
        return packageId;
    }

    /**
     * @param packageId идентификатор пакета
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    /**
     * @return версия пакета
     */
    public String getPackageVersion() {
        return packageVersion == null ? null : packageVersion.toString();
    }

    /**
     * @param packageVersion версия пакета
     * @throws NugetFormatException версия пакета не соответствует формату
     */
    public void setPackageVersion(String packageVersion) throws NugetFormatException {
        this.packageVersion = Version.parse(packageVersion);
    }

    /**
     * @return идентификатор хранилища
     */
    public int getStorageId() {
        return storageId;
    }

    /**
     * @param storageId идентификатор хранилища
     */
    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    /**
     * @return описание пакета
     */
    public String getDescription() {
        return nuspec.getDescription();
    }
}

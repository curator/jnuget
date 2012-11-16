package ru.aristar.jnuget.ui;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.nuspec.Dependency;
import ru.aristar.jnuget.files.nuspec.NuspecFile;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;
import ru.aristar.jnuget.ui.tree.TreeComponent;

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
        PackageSource packageSource;
        if (storageId == -1) {
            packageSource = PackageSourceFactory.getInstance().getPackageSource();
        } else {
            packageSource = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        }
        nupkg = packageSource.getPackage(packageId, packageVersion);
        nuspec = nupkg == null ? null : nupkg.getNuspecFile();
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

    /**
     * @return размер пакета в мегабайтах
     */
    public BigDecimal getSize() {
        BigDecimal result = new BigDecimal(nupkg.getSize());
        result = result.divide(BigDecimal.valueOf(1024 * 1024));
        result = result.setScale(2, RoundingMode.UP);
        return result;
    }

    /**
     * @return краткое описание пакета
     */
    public String getTitle() {
        return nuspec.getTitle();
    }

    /**
     * @return авторы пакета
     */
    public String getAuthors() {
        return nuspec.getAuthors();
    }

    /**
     * @return владельцы пакета
     */
    public String getOwners() {
        return nuspec.getOwners();
    }

    /**
     * @return URL иконки проекта
     */
    public String getIconUrl() {
        if (nuspec.getIconUrl() == null) {
            return "Images/packageDefaultIcon.png";
        } else {
            return nuspec.getIconUrl();
        }
    }

    /**
     * @return URL основного сайта проекта
     */
    public String getProjectUrl() {
        return nuspec.getProjectUrl();
    }

    /**
     * @return требуется или нет подтверждение лицензии
     */
    public boolean isRequireLicenseAcceptance() {
        return nuspec.isRequireLicenseAcceptance();
    }

    /**
     * @return URl по которому можно получить лицензию проекта
     */
    public String getLicenseUrl() {
        return nuspec.getLicenseUrl();
    }

    /**
     * @return аннотация пакета
     */
    public String getSummary() {
        return nuspec.getSummary();
    }

    /**
     * @return примечания к релизу
     */
    public String getReleaseNotes() {
        return nuspec.getReleaseNotes();
    }

    /**
     * @return права на пакет
     */
    public String getCopyright() {
        return nuspec.getCopyright();
    }

    public String getLanguage() {
        return nuspec.getLanguage();
    }

    public String getTags() {
        return Joiner.on(", ").join(nuspec.getTags());
    }

    /**
     * @return список зависимостей пакета
     */
    public DataModel<Dependency> getDependencies() {
        DataModel<Dependency> dependencys = new ListDataModel<>(nuspec.getDependencies());
        return dependencys;
    }

    /**
     * @return контекст приложения в сервере
     */
    private ExternalContext getContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    /**
     * @return URI приложения на сервере
     * @throws URISyntaxException ошибка получения URI прилодения
     */
    private URI getApplicationUri() throws URISyntaxException {
        ExternalContext context = getContext();
        URI uri = new URI(context.getRequestScheme(),
                null,
                context.getRequestServerName(),
                context.getRequestServerPort(),
                context.getRequestContextPath(),
                null,
                null);
        return uri;
    }

    /**
     * @return корневой URL хранилища
     * @throws URISyntaxException ошибка получения URI прилодения
     */
    public String getRootUrl() throws URISyntaxException {
        return getApplicationUri().getPath();
    }

    /**
     * @return заглушка
     */
    public TreeComponent.TreeNode getRootFileNode() {
        try {
            NupkgContentTree contentTree = new NupkgContentTree(nupkg);
            return contentTree.getRootNode();
        } catch (IOException e) {
            return new Node();
        }
    }

    public String getDownloadUrl(TreeComponent.TreeNode node) {
        return "/downloadPart/"+storageId+"/FluentAssertions/1.6.0";
    }

    /**
     * Заглушка
     */
    public static class Node implements TreeComponent.TreeNode {

        /**
         * @return имя узла
         */
        public String getName() {
            return "Root";
        }

        @Override
        public Collection<TreeComponent.TreeNode> getChildren() {
            return new ArrayList<>();
        }
    }
}

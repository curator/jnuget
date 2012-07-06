package ru.aristar.jnuget.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.Common.StorageOptions;
import ru.aristar.jnuget.sources.IndexedPackageSource;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "storageOptionsBean")
@RequestScoped
public class StorageOptionsBean implements Serializable {

    private Integer storageId;
    private PackageSource packageSource;
    private IndexedPackageSource indexDecorator;

    public void init() {
        packageSource = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        if (packageSource instanceof IndexedPackageSource) {
            indexDecorator = (IndexedPackageSource) packageSource;
            packageSource = indexDecorator.getUnderlyingSource();
        }
    }

    public Integer getStorageId() {
        return storageId;
    }

    public String getClassName() {
        return packageSource.getClass().getCanonicalName();
    }

    public boolean isIndexed() {
        return indexDecorator != null;
    }
    
    public void setIndexed(boolean value){
        indexDecorator = new IndexedPackageSource();
        indexDecorator.setUnderlyingSource(packageSource);
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public StorageOptions getStorageOptions() {
        return PackageSourceFactory.getInstance().getOptions().getStorageOptionsList().get(storageId);
    }

    public DataModel<Map.Entry<String, String>> getStorageProperties() {
        ArrayList<Map.Entry<String, String>> data = new ArrayList<>(getStorageOptions().getProperties().entrySet());
        return new ListDataModel<>(data);
    }
}

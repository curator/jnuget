package ru.aristar.jnuget.ui;

import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ru.aristar.jnuget.Common.StorageOptions;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "storageOptionsBean")
@SessionScoped
public class StorageOptionsBean implements Serializable {

    private Integer storageId;

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public StorageOptions getStorageOptions() {
        return PackageSourceFactory.getInstance().getOptions().getStorageOptionsList().get(storageId);
    }

    public Map<String, String> getStorageProperties() {        
        return getStorageOptions().getProperties();
    }
}

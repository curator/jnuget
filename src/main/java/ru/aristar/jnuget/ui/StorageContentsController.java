package ru.aristar.jnuget.ui;

import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "storageContents")
@RequestScoped
public class StorageContentsController {

    private int storageId;
    private DataModel<Nupkg> packages;
    private PackageSource<Nupkg> storage;

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public int getPackageCount() {
        return storage.getPackages().size();
    }

    public void init() {
        storage = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        ArrayList<Nupkg> nupkgs = new ArrayList<>(storage.getPackages());
        packages = new ListDataModel<>(nupkgs);
    }

    public DataModel<Nupkg> getPackages() {
        return packages;
    }
}

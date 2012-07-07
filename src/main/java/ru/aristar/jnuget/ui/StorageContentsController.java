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
 * Контроллер содержимого хранилища
 *
 * @author sviridov
 */
@ManagedBean(name = "storageContents")
@RequestScoped
public class StorageContentsController {

    /**
     * Идентификатор хранилища
     */
    private int storageId;
    /**
     * Список пакетов в хранилище
     */
    private DataModel<Nupkg> packages;
    /**
     * Хранилище
     */
    private PackageSource<Nupkg> storage;

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
     * @return количество пакетов в хранилище
     */
    public int getPackageCount() {
        return storage.getPackages().size();
    }

    /**
     * Инициализация хранилища
     */
    public void init() {
        storage = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        ArrayList<Nupkg> nupkgs = new ArrayList<>(storage.getPackages());
        packages = new ListDataModel<>(nupkgs);
    }

    /**
     * @return список пакетов в хранилище
     */
    public DataModel<Nupkg> getPackages() {
        return packages;
    }
}

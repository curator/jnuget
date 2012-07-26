package ru.aristar.jnuget.ui;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.common.Options;
import ru.aristar.jnuget.common.StorageOptions;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Контроллер списка хранилищ
 *
 * @author sviridov
 */
@ManagedBean(name = "storagesList")
@RequestScoped
public class StoragesListController implements Serializable {

    /**
     * Список настроек хранилищ
     */
    private DataModel<StorageOptions> dataModel;

    /**
     * Инициализация настроек хранилищ
     */
    @PostConstruct
    public void init() {
        List<StorageOptions> options = PackageSourceFactory.getInstance().getOptions().getStorageOptionsList();
        dataModel = new ListDataModel<>(options);
    }

    /**
     * @return количество источников пакетов
     */
    public int getRepositoriesCount() {
        return dataModel.getRowCount();
    }

    /**
     * @return список настроек хранилищ
     */
    public DataModel<StorageOptions> getStorageOptions() {
        return dataModel;
    }

    /**
     * @return корневая папка NuGet (содержит в себе служебные файлы)
     */
    public String getNugetHome() {
        return Options.getNugetHome().getAbsolutePath();
    }
}

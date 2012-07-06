package ru.aristar.jnuget.ui;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.Common.Options;
import ru.aristar.jnuget.Common.StorageOptions;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean
@RequestScoped
public class OptionsBean implements Serializable {

    private DataModel<StorageOptions> dataModel;
    
    @PostConstruct
    public void init(){
        List<StorageOptions> options = PackageSourceFactory.getInstance().getOptions().getStorageOptionsList();
        dataModel = new ListDataModel<>(options);
    }
    
    public int getRepositoriesCount() {
        return dataModel.getRowCount();
    }

    public DataModel<StorageOptions> getStorageOptions() {
        return dataModel;
    }

    public String getNugetHome() {
        return Options.getNugetHome().getAbsolutePath();
    }
}

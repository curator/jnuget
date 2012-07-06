package ru.aristar.jnuget.ui;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ru.aristar.jnuget.Common.Options;
import ru.aristar.jnuget.Common.StorageOptions;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean
@SessionScoped
public class OptionsBean implements Serializable {

    public int getRepositoriesCount() {
        return PackageSourceFactory.getInstance().getOptions().getStorageOptionsList().size();
    }

    public List<StorageOptions> getStorageOptions() {
        return PackageSourceFactory.getInstance().getOptions().getStorageOptionsList();
    }

    public String getNugetHome() {
        return Options.getNugetHome().getAbsolutePath();
    }
}

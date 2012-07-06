package ru.aristar.jnuget.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.MediaType;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
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

    @PostConstruct
    public void init() {
        storage = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
    }

    public Collection<Nupkg> getPackages() {
        return storage.getPackages();
    }

    public void downloadPackage(Nupkg nupkg) throws IOException {
        System.out.println("получение пакета " + nupkg);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType(MediaType.APPLICATION_OCTET_STREAM);
        externalContext.setResponseContentLength(nupkg.getSize().intValue());
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + nupkg.getFileName() + "\"");
        OutputStream outputStream = externalContext.getResponseOutputStream();
        InputStream inputStream = nupkg.getStream();
        TempNupkgFile.fastChannelCopy(Channels.newChannel(inputStream), Channels.newChannel(outputStream));
        facesContext.responseComplete();
    }

 
}

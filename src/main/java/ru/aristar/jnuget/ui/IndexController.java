package ru.aristar.jnuget.ui;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.Manifest;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "index")
@ApplicationScoped
public class IndexController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExternalContext getContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

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

    public String getVersion() {
        String vesion = null;
        try {
            InputStream inputStream = getContext().getResourceAsStream("/META-INF/MANIFEST.MF");
            if (inputStream != null) {
                Manifest manifest = new Manifest(inputStream);
                vesion = manifest.getMainAttributes().getValue("Implementation-Version");
            }
        } catch (Exception e) {
            logger.warn("Ошибка определения версии пакета", e);
        }
        vesion = vesion == null ? "" : "v" + vesion;
        return vesion;
    }

    public String getStrorageGetUrl() throws URISyntaxException {
        return getApplicationUri().toASCIIString() + "/nuget/nuget";
    }

    public String getStroragePutUrl() throws URISyntaxException {
        return getApplicationUri().toASCIIString() + "/nuget/";
    }
}

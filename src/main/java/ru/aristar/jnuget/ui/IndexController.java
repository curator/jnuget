package ru.aristar.jnuget.ui;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.Manifest;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Контроллер для индексной страницы
 *
 * @author sviridov
 */
@ManagedBean(name = "index")
@ApplicationScoped
public class IndexController {

    /**
     * Логгер
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
     * @return версия приложения
     */
    public String getVersion() {
        try {
            InputStream inputStream = getContext().getResourceAsStream("/META-INF/MANIFEST.MF");
            if (inputStream != null) {
                Manifest manifest = new Manifest(inputStream);
                String vesion = manifest.getMainAttributes().getValue("Implementation-Version");
                return "v" + vesion;
            } else {
                return "";
            }
        } catch (Exception e) {
            logger.warn("Ошибка определения версии пакета", e);
            return "";
        }
    }

    /**
     * @return URL, по которому можно получать пакеты
     * @throws URISyntaxException ошибка получения URI прилодения
     */
    public String getStrorageGetUrl() throws URISyntaxException {
        return getApplicationUri().toASCIIString() + "/nuget/nuget";
    }

    /**
     * @return URL, по которому можно публиковать пакеты
     * @throws URISyntaxException ошибка получения URI прилодения
     */
    public String getStroragePutUrl() throws URISyntaxException {
        return getApplicationUri().toASCIIString() + "/nuget/";
    }
}

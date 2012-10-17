package ru.aristar.jnuget.ui;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import ru.aristar.jnuget.common.ProxyOptions;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "commonOptions")
@RequestScoped
public class CommonOptionsController {

    /**
     * @return true, если используется системный прокси
     */
    public boolean isUseSystemProxy() {
        String useSystemProxy = System.getProperty("java.net.useSystemProxies");
        return useSystemProxy != null && useSystemProxy.equalsIgnoreCase("true");
    }

    /**
     * @param value true, если используется системный прокси
     */
    public void setUseSystemProxy(boolean value) {
    }

    /**
     * @return true, если прокси не используется
     */
    public boolean isNoUseProxy() {
        //TODO Реализовать настройку с отключенным прокси
        return false;
    }

    /**
     * @param value true, если прокси не используется
     */
    public void setNoUseProxy(boolean value) {
    }

    /**
     * @return хост прокси
     */
    public String getProxyHost() {
        return getProxyOptions().getHost();
    }

    /**
     * @return порт прокси
     */
    public Integer getProxyPort() {
        return getProxyOptions().getPort();
    }

    /**
     * @return настройки прокси
     */
    private ProxyOptions getProxyOptions() {
        return PackageSourceFactory.getInstance().getOptions().getProxyOptions();
    }
}

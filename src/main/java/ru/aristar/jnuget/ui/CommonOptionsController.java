package ru.aristar.jnuget.ui;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import ru.aristar.jnuget.common.CustomProxySelector;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "commonOptions")
@RequestScoped
public class CommonOptionsController {

    /**
     * @return текущие настройки прокси в системе
     */
    private CustomProxySelector getProxySelector() {
        ProxySelector proxySelector = ProxySelector.getDefault();
        if (proxySelector instanceof CustomProxySelector) {
            return (CustomProxySelector) proxySelector;
        } else {
            return null;
        }
    }

    /**
     * @return текущий используемый прокси (первый из списка доступных)
     */
    private Proxy getProxy() {
        CustomProxySelector proxySelector = getProxySelector();
        if (proxySelector == null) {
            return null;
        }
        List<Proxy> proxys = proxySelector.select(null);
        if (proxys != null && !proxys.isEmpty()) {
            return proxys.get(0);
        } else {
            return null;
        }
    }

    /**
     * @return адрес прокси
     */
    private InetSocketAddress getSocketAddress() {
        Proxy proxy = getProxy();
        if (proxy == null) {
            return null;
        }
        SocketAddress address = proxy.address();
        if (address instanceof InetSocketAddress) {
            return (InetSocketAddress) address;
        } else {
            return null;
        }
    }

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
        return getProxy() == Proxy.NO_PROXY;
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
        InetSocketAddress address = getSocketAddress();
        return address == null ? null : address.getHostName();
    }

    /**
     * @return порт прокси
     */
    public Integer getProxyPort() {
        InetSocketAddress address = getSocketAddress();
        return address == null ? null : address.getPort();
    }
}

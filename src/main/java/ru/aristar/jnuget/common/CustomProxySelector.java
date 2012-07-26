package ru.aristar.jnuget.common;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс Натроек прокси
 *
 * @author sviridov
 */
public class CustomProxySelector extends ProxySelector {

    /**
     * Список прокси
     */
    private List<Proxy> proxys = new ArrayList<>();

    /**
     * Конструктор по умолчанию (не использовать прокси)
     */
    public CustomProxySelector() {
        Proxy proxy = Proxy.NO_PROXY;
        proxys.add(proxy);
    }

    /**
     * Конструктор, задающий прокси
     *
     * @param host хост прокси сервера
     * @param port порт прокси сервера
     */
    public CustomProxySelector(String host, int port) {
        SocketAddress address = new InetSocketAddress(host, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        proxys.add(proxy);
    }

    /**
     * Возвразает список прокси для указанного URI
     *
     * @param uri адрес сервера, для которого нужно получить прокси
     * @return список прокси
     */
    @Override
    public List<Proxy> select(URI uri) {
        return proxys;
    }

    /**
     * Ошибка соединения с сервером
     *
     * @param uri
     * @param address
     * @param exception
     */
    @Override
    public void connectFailed(URI uri, SocketAddress address, IOException exception) {
    }
}

package ru.aristar.jnuget.Common;

import javax.xml.bind.annotation.*;

/**
 * Настройки прокси
 *
 * @author sviridov
 */
@XmlRootElement(name = "proxy")
@XmlAccessorType(XmlAccessType.NONE)
public class ProxyOptions {

    /**
     * Не использовать прокси
     */
    @XmlAttribute(name = "noProxy")
    private Boolean noProxy;
    /**
     * Использовать системный прокси
     */
    @XmlAttribute(name = "useSystemProxy")
    private Boolean useSystemProxy;
    /**
     * Хост прокси сервера
     */
    @XmlElement(name = "host")
    private String host;
    /**
     * Порт прокси сервера
     */
    @XmlElement(name = "port")
    private Integer port;
    /**
     * Логин на прокси сервере
     */
    @XmlElement(name = "login")
    private String login;
    /**
     * Пароль на прокси сервере
     */
    @XmlElement(name = "password")
    private String password;

    /**
     * @return хост прокси сервера
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host хост прокси сервера
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return логин на прокси сервере
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login логин на прокси сервере
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return не использовать прокси
     */
    public Boolean getNoProxy() {
        return noProxy;
    }

    /**
     * @param noProxy не использовать прокси
     */
    public void setNoProxy(Boolean noProxy) {
        this.noProxy = noProxy;
    }

    /**
     * @return пароль на прокси сервере
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password пароль на прокси сервере
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return порт прокси сервера
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port порт прокси сервера
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return использовать системный прокси
     */
    public Boolean getUseSystemProxy() {
        return useSystemProxy;
    }

    /**
     * @param useSystemProxy Использовать системный прокси
     */
    public void setUseSystemProxy(Boolean useSystemProxy) {
        this.useSystemProxy = useSystemProxy;
    }
}

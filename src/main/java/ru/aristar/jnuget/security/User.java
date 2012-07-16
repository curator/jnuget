package ru.aristar.jnuget.security;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.*;

/**
 *
 * @author sviridov
 */
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
public class User {

    /**
     * Имя пользователя
     */
    @XmlAttribute(name = "name")
    private String name;
    /**
     * Пароль пользователя
     */
    @XmlAttribute(name = "password")
    private String password;
    /**
     * Ключ доступа
     */
    @XmlAttribute(name = "apiKey")
    private String apiKey;
    /**
     * Роли пользователя
     */
    @XmlElement(name = "role")
    private Set<String> roles;

    /**
     * @return ключ доступа
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey ключ доступа
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return роли пользователя
     */
    public Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }

    /**
     * @param roles роли пользователя
     */
    public void setRoles(Set<String> roles) {

        this.roles = roles;
    }

    /**
     * @return имя пользователя
     *
     */
    public String getName() {
        return name;
    }

    /**
     * @param name имя пользователя
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Генерировать новый ключ
     */
    public void generateApiKey() {
        //'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee'
        throw new UnsupportedOperationException("Метод не реализован");
    }
}

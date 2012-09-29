package ru.aristar.jnuget.security;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
     * Строки с ролями пользователя
     */
    @XmlElement(name = "role")
    private Set<String> roleStrings;
    /**
     * Роли пользователя
     */
    private EnumSet<Role> roles;

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
    public EnumSet<Role> getRoles() {
        if (roles == null) {
            roles = EnumSet.noneOf(Role.class);
            if (roleStrings != null && !roleStrings.isEmpty()) {
                roles = EnumSet.noneOf(Role.class);
                for (String roleName : roleStrings) {
                    Role role = Role.findRole(roleName);
                    if (role != null) {
                        roles.add(role);
                    }
                }
            }
        }
        return roles;
    }

    /**
     * @param roles роли пользователя
     */
    public void setRoles(EnumSet<Role> roles) {
        this.roles = roles;
        if (roles == null || roles.isEmpty()) {
            return;
        }
        roleStrings = new HashSet<>(roles.size());
        for (Role role : roles) {
            roleStrings.add(role.getName());
        }
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

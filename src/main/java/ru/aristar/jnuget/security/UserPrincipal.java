package ru.aristar.jnuget.security;

import java.security.Principal;
import java.util.Objects;

/**
 * Параметры пользователя
 *
 * @author sviridov
 */
public class UserPrincipal implements Principal {

    /**
     * Имя пользователя
     */
    private final String name;

    /**
     * @param name имя пользователя
     */
    public UserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserPrincipal other = (UserPrincipal) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.name);
        return hash;
    }
}

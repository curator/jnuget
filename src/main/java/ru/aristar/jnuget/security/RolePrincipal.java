package ru.aristar.jnuget.security;

import java.security.Principal;
import java.util.Objects;

/**
 * Параметры роли
 *
 * @author sviridov
 */
public class RolePrincipal implements Principal {

    /**
     * Имя роли
     */
    private final String name;

    /**
     * @param name имя роли
     */
    public RolePrincipal(String name) {
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
        final RolePrincipal other = (RolePrincipal) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.name);
        return hash;
    }
}

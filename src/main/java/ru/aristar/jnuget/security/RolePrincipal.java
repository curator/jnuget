package ru.aristar.jnuget.security;

import java.security.Principal;

/**
 * Параметры роли
 *
 * @author sviridov
 */
public class RolePrincipal implements Principal {

    /**
     * Роль
     */
    private final Role role;

    /**
     * @param role роль
     */
    public RolePrincipal(Role role) {
        this.role = role;
    }

    /**
     * @param name имя роли
     */
    public RolePrincipal(String name) {
        this.role = Role.findRole(name);
    }

    @Override
    public String getName() {
        return role.getName();
    }

    /**
     * @return роль
     */
    public Role getRole() {
        return role;
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
        if (this.role != other.role) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.role != null ? this.role.hashCode() : 0);
        return hash;
    }
}

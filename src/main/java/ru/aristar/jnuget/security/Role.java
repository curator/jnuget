package ru.aristar.jnuget.security;

import java.util.EnumSet;

/**
 * Роли
 *
 * @author sviridov
 */
public enum Role {

    /**
     * Роль администратора сервера
     */
    Administrator("jnuget-admin", "GuiUser", "Push", "Read", "Delete"),
    /**
     * Роль, для которой доступен просмотр GUI
     */
    GuiUser("jnuget-gui"),
    /**
     * Роль, для которой разрешена публикация пакетов
     */
    Push("jnuget-push"),
    /**
     * Роль, для которой разрешено чтение пакетов
     */
    Read("jnuget-read"),
    /**
     * Роль, для которой разрешено удаление пакетов
     */
    Delete("jnuget-delete"),
    /**
     * Роль, не являющаяся ролью системы
     */
    Uncnown("uncnown");

    /**
     * Поиск роли по ее имени
     *
     * @param name имя роли
     * @return роль, если не найдена - роль <b>Uncnown</b>
     */
    static Role findRole(String name) {
        for (Role role : values()) {
            if (role.name.equals(name)) {
                return role;
            }
        }
        return Uncnown;
    }
    /**
     * Имя роли
     */
    private final String name;
    /**
     * Роли, которые включает текущая роль
     */
    private volatile EnumSet<Role> includedRoles;
    /**
     * Имена ролей, которые включает текущая роль
     */
    private final String[] includedRolesNames;

    /**
     * @param name имя роли
     * @param includedRolesNames роли, которые включает текущая роль
     */
    private Role(String name, String... includedRolesNames) {
        this.name = name;
        this.includedRolesNames = includedRolesNames;
    }

    /**
     * @return роли, которые включает текущая роль
     */
    public EnumSet<Role> getIncludedRoles() {
        if (includedRoles == null) {
            synchronized (includedRolesNames) {
                if (includedRoles == null) {
                    this.includedRoles = EnumSet.noneOf(Role.class);
                    for (String roleName : includedRolesNames) {
                        this.includedRoles.add(Role.valueOf(roleName));
                    }
                }
            }
        }
        return includedRoles;
    }

    /**
     * @return имя роли
     */
    public String getName() {
        return name;
    }

    /**
     * Включает ли в себя роль
     *
     * @param role роль, которая может быть включена в текущую
     * @return true, если текущая роль включает указанную
     */
    public boolean contains(Role role) {
        if (this == role) {
            return true;
        }
        for (Role includedRole : getIncludedRoles()) {
            if (includedRole == role) {
                return true;
            }
        }
        return false;
    }
}

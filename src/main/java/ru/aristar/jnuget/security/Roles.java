package ru.aristar.jnuget.security;

/**
 * Роли
 *
 * @author sviridov
 */
public enum Roles {

    /**
     * Роль администратора сервера
     */
    Administrator("jnuget-admin"),
    /**
     * Роль, для которой доступен просмотр GUI
     */
    GuiUser("jnuget-gui");
    private final String name;

    /**
     * @param name имя роли
     */
    private Roles(String name) {
        this.name = name;
    }

    /**
     * @return имя роли
     */
    public String getName() {
        return name;
    }
}

package ru.aristar.jnuget.security;

/**
 * Роли
 *
 * @author sviridov
 */
public enum Role {

    /**
     * Роль администратора сервера
     */
    Administrator("jnuget-admin"),
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
     * @param name имя роли
     */
    private Role(String name) {
        this.name = name;
    }

    /**
     * @return имя роли
     */
    public String getName() {
        return name;
    }
}

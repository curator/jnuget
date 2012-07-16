package ru.aristar.jnuget.Common;

/**
 *
 * @author sviridov
 */
public enum Roles {

    Administrator("jnuget-admin"),
    GuiUser("jnuget-gui");
    private final String name;

    private Roles(String name) {
        this.name = name;
    }
}

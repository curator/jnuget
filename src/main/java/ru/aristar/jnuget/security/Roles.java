package ru.aristar.jnuget.security;

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

    public String getName() {
        return name;
    }
    
    
}

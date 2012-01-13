package ru.aristar.jnuget.rss;

/**
 *
 * @author sviridov
 */
public enum MIcrosoftTypes {

    Int32("Edm.Int32");

    private MIcrosoftTypes(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
    private final String typeName;
}

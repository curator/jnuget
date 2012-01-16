package ru.aristar.jnuget.rss;

/**
 *
 * @author sviridov
 */
public enum MicrosoftTypes {

    Int32("Edm.Int32"),
    Double("Edm.Double"),
    Boolean("Edm.Boolean"),
    String("Edm.String"),
    DateTime("Edm.DateTime");

    public static MicrosoftTypes parse(String string) {
        for (MicrosoftTypes mt : values()) {
            if (mt.toString().equals(string)) {
                return mt;
            }
        }
        return null;
    }

    private MicrosoftTypes(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
    private final String typeName;
}

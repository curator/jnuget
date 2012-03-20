package ru.aristar.jnuget.files;

import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class VersionRange {

    private Version lowVersion;
    private BorderType lowBorderType;
    private BorderType topBorderType;
    private Version topVersion;

    public enum BorderType {

        /**
         * Граница включается
         */
        INCLUDE("[", "]"),
        /**
         * Граница исключается
         */
        EXCLUDE("(", ")");
        /**
         * Символ левой границы
         */
        private final String leftBorderSymbol;
        /**
         * Символ правой границы
         */
        private final String rightBorderSymbol;

        private BorderType(String leftBorderSymbol, String rightBorderSymbol) {
            this.leftBorderSymbol = leftBorderSymbol;
            this.rightBorderSymbol = rightBorderSymbol;
        }

        public String getLeftBorderSymbol() {
            return leftBorderSymbol;
        }

        public String getRightBorderSymbol() {
            return rightBorderSymbol;
        }
    }

    public Version getLowVersion() {
        return lowVersion;
    }

    public void setLowVersion(Version lowVersion) {
        this.lowVersion = lowVersion;
    }

    public Version getTopVersion() {
        return topVersion;
    }

    public void setTopVersion(Version topVersion) {
        this.topVersion = topVersion;
    }

    public BorderType getLowBorderType() {
        return lowBorderType;
    }

    public void setLowBorderType(BorderType lowBorderType) {
        this.lowBorderType = lowBorderType;
    }

    public BorderType getTopBorderType() {
        return topBorderType;
    }

    public void setTopBorderType(BorderType topBorderType) {
        this.topBorderType = topBorderType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(lowBorderType.leftBorderSymbol);
        builder.append(lowVersion.toString());
        builder.append(";");
        builder.append(topVersion.toString());
        builder.append(topBorderType.rightBorderSymbol);
        return builder.toString();
    }

    private static VersionRange parse(String versionRangeString) {
        return null;
    }
}

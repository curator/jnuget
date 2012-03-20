package ru.aristar.jnuget.files;

import ru.aristar.jnuget.Version;

/**
 * 1.0 = 1.0 ≤ x </p> (,1.0] = x ≤ 1.0 </p> (,1.0) = x < 1.0 </p> [1.0] = x ==
 * 1.0 </p> (1.0) = invalid </p> (1.0,) = 1.0 < x </p> (1.0,2.0) = 1.0 < x < 2.0
 * </p> [1.0,2.0] = 1.0 ≤ x ≤ 2.0 </p> empty = latest version.
 *
 * @author sviridov
 */
public class VersionRange {

    /**
     * Разделитель границ отрезка
     */
    public static final String BORDER_DELIMETER = ",";
    /**
     * Версия на нижней границе
     */
    private Version lowVersion;
    /**
     * Тип нижней границы
     */
    private BorderType lowBorderType;
    /**
     * Тип верхней границы
     */
    private BorderType topBorderType;
    /**
     * Версия на верхней границе
     */
    private Version topVersion;

    /**
     * @return диапазон указывает на последнюю версию пакета
     */
    public boolean isLastVersion() {
        return lowVersion == null && topVersion == null;
    }

    /**
     * @return диапазон указывает на конкретную версию пакета
     */
    public boolean isFixedVersion() {
        return lowVersion != null
                && topVersion != null
                && lowVersion.equals(topVersion);
    }

    /**
     * @return диапазон указывает на конкретную или большую версию пакета
     */
    public boolean isSimpleRange() {
        return topVersion == null
                && lowVersion != null
                && lowBorderType == BorderType.INCLUDE;
    }

    /**
     * Тип границы диапазона
     */
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
         * Символ нижней границы
         */
        private final String lowBorderSymbol;
        /**
         * Символ верхней границы
         */
        private final String topBorderSymbol;

        /**
         * @param lowBorderSymbol символ, обозначающий нижнюю границу
         * @param topBorderSymbol символ, обозначающий верхнюю границу
         */
        private BorderType(String lowBorderSymbol, String topBorderSymbol) {
            this.lowBorderSymbol = lowBorderSymbol;
            this.topBorderSymbol = topBorderSymbol;
        }

        /**
         * @return Символ нижней границы
         */
        public String getLowBorderSymbol() {
            return lowBorderSymbol;
        }

        /**
         * @return Символ верхней границы
         */
        public String getTopBorderSymbol() {
            return topBorderSymbol;
        }
    }

    /**
     * @return Версия на нижней границе
     */
    public Version getLowVersion() {
        return lowVersion;
    }

    /**
     * @param lowVersion Версия на нижней границе
     */
    public void setLowVersion(Version lowVersion) {
        this.lowVersion = lowVersion;
    }

    /**
     * @return Версия на верхней границе
     */
    public Version getTopVersion() {
        return topVersion;
    }

    /**
     * @param topVersion Версия на верхней границе
     */
    public void setTopVersion(Version topVersion) {
        this.topVersion = topVersion;
    }

    /**
     * @return Тип нижней границы
     */
    public BorderType getLowBorderType() {
        return lowBorderType;
    }

    /**
     * @param lowBorderType Тип нижней границы
     */
    public void setLowBorderType(BorderType lowBorderType) {
        this.lowBorderType = lowBorderType;
    }

    /**
     * @return Тип верхней границы
     */
    public BorderType getTopBorderType() {
        return topBorderType;
    }

    /**
     * @param topBorderType Тип верхней границы
     */
    public void setTopBorderType(BorderType topBorderType) {
        this.topBorderType = topBorderType;
    }

    /**
     * @return строковое представление диапазона версий
     */
    @Override
    public String toString() {
        if (isLastVersion()) {
            return "";
        }

        if (isFixedVersion()) {
            return "[" + topVersion.toString() + "]";
        }

        if (isSimpleRange()) {
            return lowVersion.toString();
        }

        StringBuilder builder = new StringBuilder();
        if (lowVersion != null) {
            builder.append(lowBorderType.lowBorderSymbol);
            builder.append(lowVersion.toString());
        } else {
            builder.append(BorderType.EXCLUDE.lowBorderSymbol);
        }
        builder.append(BORDER_DELIMETER);
        if (topVersion != null) {
            builder.append(topVersion.toString());
            builder.append(topBorderType.topBorderSymbol);
        } else {
            builder.append(BorderType.EXCLUDE.topBorderSymbol);
        }
        return builder.toString();
    }

    private static VersionRange parse(String versionRangeString) {
        return null;
    }
}

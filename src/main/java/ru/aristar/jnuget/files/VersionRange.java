package ru.aristar.jnuget.files;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.Version;

/**
 * Диапазон версий
 *
 * <p> 1.0 = 1.0 ≤ x</p>
 * <p>(,1.0] = x ≤ 1.0</p>
 * <p>(,1.0) = x < 1.0</p>
 * <p>[1.0] = x == 1.0</p>
 * <p>(1.0) = invalid</p>
 * <p>(1.0,) = 1.0 < x</p>
 * <p>(1.0,2.0) = 1.0< x < 2.0</p>
 * <p>[1.0,2.0] = 1.0 ≤ x ≤ 2.0</p>
 * <p>empty = latest version</p>.
 *
 * @author sviridov
 */
public class VersionRange implements Serializable{

    /**
     * Разделитель границ отрезка
     */
    public static final String BORDER_DELIMETER = ",";
    /**
     * Полный шаблон диапазона версий
     */
    public static final String FULL_VERSION_RANGE_PATTERN = "(?<leftBorder>[\\(\\[])(?<left>("
            + Version.VERSION_FORMAT + ")?)"
            + BORDER_DELIMETER
            + "(?<right>(" + Version.VERSION_FORMAT + ")?)(?<rightBorder>[\\)\\]])";
    /**
     * Шаблон фиксированной версии
     */
    public static final String FIXED_VERSION_RANGE_PATTERN = "\\[(" + Version.VERSION_FORMAT + ")\\]";
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
    public boolean isLatestVersion() {
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
     * Конструктор по умолчанию (все поля null)
     */
    public VersionRange() {
    }

    /**
     * @param lowVersion Версия на нижней границе
     * @param lowBorderType Тип нижней границы
     * @param topVersion Версия на верхней границе
     * @param topBorderType Тип верхней границы
     */
    public VersionRange(Version lowVersion, BorderType lowBorderType, Version topVersion, BorderType topBorderType) {
        this.lowVersion = lowVersion;
        this.lowBorderType = lowBorderType;
        this.topBorderType = topBorderType;
        this.topVersion = topVersion;
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
         * Распознает тип границы
         *
         * @param borderSymbol
         * @return тип границы
         */
        private static BorderType getBorderType(String borderSymbol) {
            if (borderSymbol == null || borderSymbol.isEmpty()) {
                return null;
            } else if (borderSymbol.equals(INCLUDE.lowBorderSymbol)
                    || borderSymbol.equals(INCLUDE.topBorderSymbol)) {
                return INCLUDE;
            } else if (borderSymbol.equals(EXCLUDE.lowBorderSymbol)
                    || borderSymbol.equals(EXCLUDE.topBorderSymbol)) {
                return EXCLUDE;
            } else {
                return null;
            }
        }
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
        if (isLatestVersion()) {
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

    /**
     * Распознает строку и возвращает диапазон версий
     *
     * @param versionRangeString строка диапазона версий
     * @return диапазон версий
     * @throws NugetFormatException некорректный формат версии
     */
    public static VersionRange parse(String versionRangeString) throws NugetFormatException {
        if (versionRangeString == null || versionRangeString.isEmpty()) {
            return new VersionRange();
        }
        if (Version.isValidVersionString(versionRangeString)) {
            Version version = Version.parse(versionRangeString);
            return new VersionRange(version, BorderType.INCLUDE, null, null);
        }

        Pattern fixedVersionPattern = Pattern.compile("^" + FIXED_VERSION_RANGE_PATTERN + "$");
        Matcher fixedVersionMatcher = fixedVersionPattern.matcher(versionRangeString);
        if (fixedVersionMatcher.matches()) {
            Version version = Version.parse(fixedVersionMatcher.group(1));
            return new VersionRange(version, BorderType.INCLUDE, version, BorderType.INCLUDE);
        }

        Pattern pattern = Pattern.compile("^" + FULL_VERSION_RANGE_PATTERN + "$");
        Matcher matcher = pattern.matcher(versionRangeString);
        if (matcher.matches()) {
            Version lowVersion = null;
            BorderType lowBorder = null;
            String lowVersionString = matcher.group("left");
            if (!lowVersionString.isEmpty()) {
                lowVersion = Version.parse(lowVersionString);
                lowBorder = BorderType.getBorderType(matcher.group("leftBorder"));
            }
            Version topVersion = null;
            BorderType topBorder = null;
            String topVersionString = matcher.group("right");
            if (!topVersionString.isEmpty()) {
                topVersion = Version.parse(topVersionString);
                topBorder = BorderType.getBorderType(matcher.group("rightBorder"));
            }
            return new VersionRange(lowVersion, lowBorder, topVersion, topBorder);
        }
        return null;
    }

    /**
     * @param obj объект, с которым производится сравнение
     * @return true, если диапазоны версий идентичны
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VersionRange other = (VersionRange) obj;
        if (!Objects.equals(this.lowVersion, other.lowVersion)) {
            return false;
        }
        if (this.lowBorderType != other.lowBorderType) {
            return false;
        }
        if (this.topBorderType != other.topBorderType) {
            return false;
        }
        if (!Objects.equals(this.topVersion, other.topVersion)) {
            return false;
        }
        return true;
    }

    /**
     * @return HASH код объекта
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.lowVersion);
        hash = 59 * hash + (this.lowBorderType != null ? this.lowBorderType.hashCode() : 0);
        hash = 59 * hash + (this.topBorderType != null ? this.topBorderType.hashCode() : 0);
        hash = 59 * hash + Objects.hashCode(this.topVersion);
        return hash;
    }
}

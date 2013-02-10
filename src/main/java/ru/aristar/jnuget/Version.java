package ru.aristar.jnuget;

import java.io.Serializable;
import static java.text.MessageFormat.format;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 * Версия пакета
 *
 * @author unlocker
 */
public class Version implements Comparable<Version>, Serializable {

    /**
     * Шаблон строки версии
     */
    public static final String VERSION_FORMAT = "(\\d+)\\.?(\\d*)\\.?(\\d*)\\.?([-\\d\\w_]*)";
    /**
     * Шаблон числа
     */
    private static final String NUMBER_PATTERN = "\\d+";
    /**
     * Выражение разбора
     */
    private static transient Pattern pattern = Pattern.compile("^" + VERSION_FORMAT + "$");
    /**
     * Шаблон числовой ревизии
     */
    private transient Pattern numberPattern = Pattern.compile(NUMBER_PATTERN);

    /**
     * Безопастно распознает majopr, minor и build
     *
     * @param group строковое значение части версии
     * @return числовое значение части версии или null
     */
    private static Integer parseInt(String group) {
        if (group == null || group.isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(group);
        }
    }
    /**
     * Основная версия
     */
    private final Integer major;
    /**
     * Минорная версия
     */
    private final Integer minor;
    /**
     * Номер сборки
     */
    private final Integer build;
    /**
     * Ревизия
     */
    private final String revision;

    /**
     * @param major основная версия
     * @param minor минорная версия
     * @param build номер сборки
     * @param revision ревизия
     */
    public Version(Integer major, Integer minor, Integer build, String revision) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
    }

    /**
     * @return основная версия
     */
    public Integer getMajor() {
        return major;
    }

    /**
     * @return минорная версия
     */
    public Integer getMinor() {
        return minor;
    }

    /**
     * @return номер сборки
     */
    public Integer getBuild() {
        return build;
    }

    /**
     * @return ревизия
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Распознает строку с версией
     *
     * @param versionString строковое представление версии
     * @return версия
     * @throws NugetFormatException строка не соответствует формату версии
     */
    public static Version parse(String versionString) throws NugetFormatException {
        if (versionString == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(versionString);
        if (!matcher.find()) {
            throw new NugetFormatException(format("Строка \"{0}\"не соответствует формату версии. ", versionString));
        }
        Integer major = parseInt(matcher.group(1));
        Integer minor = parseInt(matcher.group(2));
        Integer build = parseInt(matcher.group(3));
        String revision = null;
        if (!matcher.group(4).isEmpty()) {
            revision = matcher.group(4);
        }
        return new Version(major, minor, build, revision);
    }

    /**
     * Проверка на то, является ли строка корректной строкой версии
     *
     * @param versionRangeString строка
     * @return true, если это корректное строковое представление версии
     */
    public static boolean isValidVersionString(String versionRangeString) {
        Matcher matcher = pattern.matcher(versionRangeString);
        return matcher.matches();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Version other = (Version) obj;
        if (!Objects.equals(this.major, other.major)) {
            return false;
        }
        if (!Objects.equals(this.minor, other.minor)) {
            return false;
        }
        if (!Objects.equals(this.build, other.build)) {
            return false;
        }
        if (!Objects.equals(this.revision, other.revision)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.major);
        hash = 89 * hash + Objects.hashCode(this.minor);
        hash = 89 * hash + Objects.hashCode(this.build);
        hash = 89 * hash + Objects.hashCode(this.revision);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(major.intValue());
        if (minor != null) {
            buffer.append(".").append(minor.intValue());

            if (build != null) {
                buffer.append(".").append(build.intValue());

                if (revision != null && !revision.trim().isEmpty()) {
                    if (!revision.startsWith("-")) {
                        buffer.append(".");
                    }
                    buffer.append(revision);
                }
            }
        }


        return buffer.toString();
    }

    @Override
    public int compareTo(Version other) {
        if (other == null) {
            return 1;
        }
        int majorCompare = compareIntegerPossibleNull(this.major, other.major);
        if (majorCompare != 0) {
            return majorCompare;
        }

        int minorCompare = compareIntegerPossibleNull(this.minor, other.minor);
        if (minorCompare != 0) {
            return minorCompare;
        }

        int buildCompare = compareIntegerPossibleNull(this.build, other.build);
        if (buildCompare != 0) {
            return buildCompare;
        }

        return compareStringPossibleNull(this.revision, other.revision);
    }

    /**
     * Сравнение строк, которые могут быть null без учета регистра
     *
     * @param str1 первая строка
     * @param str2 вторая строка
     * @return [-1,0,1] </br> <ul> <li>-1 первая меньше</li> <li>0 равны </li>
     * <li>1 вторая меньше </li> <ul>
     */
    private int compareStringPossibleNull(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return 0;
        } else if (str1 == null) {
            return -1;
        } else if (str2 == null) {
            return 1;
        } else {
            boolean firstStringIsNumber = numberPattern.matcher(str1).matches();
            boolean secondStringIsNumber = numberPattern.matcher(str2).matches();
            if (firstStringIsNumber & secondStringIsNumber) {
                return Integer.compare(Integer.parseInt(str1), Integer.parseInt(str2));
            } else {
                return str1.compareToIgnoreCase(str2);
            }
        }
    }

    /**
     * Сравнение целых чисел, которые могут быть null
     *
     * @param int1 первое число
     * @param int2 второе число
     * @return [-1,0,1] </br> <ul> <li>-1 первое меньше</li> <li>0 равны </li>
     * <li>1 второе меньше </li> <ul>
     */
    private int compareIntegerPossibleNull(Integer int1, Integer int2) {
        if (int1 == null && int2 == null) {
            return 0;
        } else if (int1 == null) {
            return -1;
        } else if (int2 == null) {
            return 1;
        } else {
            return int1.compareTo(int2);
        }
    }
}

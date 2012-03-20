package ru.aristar.jnuget;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 * Версия пакета
 *
 * @author unlocker
 */
public class Version implements Comparable<Version> {

    public static final String VERSION_FORMAT = "(\\d+)\\.?(\\d*)\\.?(\\d*)\\.?(.*)";
    /**
     * Выражение разбора
     */
    private static Pattern pattern = Pattern.compile("^" + VERSION_FORMAT + "$");

    private static Integer parseInt(String group) {
        if (group == null || group.isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(group);
        }
    }
    private final Integer major;
    private final Integer minor;
    private final Integer build;
    private final String revision;

    public Version(Integer major, Integer minor, Integer build, String revision) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
    }

    Version(Object[] object) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Integer getMajor() {
        return major;
    }

    public Integer getMinor() {
        return minor;
    }

    public Integer getBuild() {
        return build;
    }

    public String getRevision() {
        return revision;
    }

    public static Version parse(String input) throws NugetFormatException {
        if (input == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new NugetFormatException("Строка не соответствует формату версии. " + input);
        }
        Integer major = parseInt(matcher.group(1));
        Integer minor = parseInt(matcher.group(2));
        Integer build = parseInt(matcher.group(3));
        String revision = matcher.group(4);
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

    public int compareTo(Version o) {
        if (this.equals(o)) {
            return 0;
        } else {
            if (this.major > o.major
                    || (this.major == o.major && compareIntegerPossibleNull(this.minor, o.minor) > 0)
                    || (this.major == o.major && this.minor == o.minor && compareIntegerPossibleNull(this.build, o.build) > 0)
                    || (this.major == o.major && this.minor == o.minor && this.build == o.build
                    && compareStringPossibleNull(this.revision, o.revision) > 0)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private int compareStringPossibleNull(String str1, String str2) {
        if (stringsNullOrEqual(str1, str2)) {
            return 0;
        } else {
            if (str1 == null && str2 != null) {
                return 1;
            } else if (str1 != null && str2 == null) {
                return -1;
            } else {
                return str1.compareToIgnoreCase(str2);
            }
        }
    }

    private int compareIntegerPossibleNull(Integer int1, Integer int2) {
        if (int1 == null && int2 == null) {
            return 0;
        } else {
            if (int1 == null && int2 != null) {
                return -1;
            } else if (int1 != null && int2 == null) {
                return 1;
            } else {
                return int1.compareTo(int2);
            }
        }
    }

    private boolean stringsNullOrEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        // Сравнение без учета регистра.
        return str1.toLowerCase().equals(str2.toLowerCase());
    }
}

package ru.aristar.jnuget.files;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import ru.aristar.jnuget.Version;

/**
 * Описание зависимости
 *
 * @author Unlocker
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Dependency implements Serializable {

    /**
     * Идентификатор пакета
     */
    @XmlAttribute(name = "id")
    public String id;
    /**
     * Версия пакета
     */
    public VersionRange versionRange;

    /**
     * @return строковое представление диапазона версий
     */
    @XmlAttribute(name = "version")
    public String getVersionRangeString() {
        if (versionRange == null) {
            return null;
        }
        return versionRange.toString();
    }

    /**
     * @param versionRangeString строковое представление диапазона версий
     * @throws NugetFormatException некорректный формат версии
     */
    public void setVersionRangeString(String versionRangeString) throws NugetFormatException {
        this.versionRange = VersionRange.parse(versionRangeString);
    }

    /**
     * @param obj объект, с которым производится сравнение
     * @return true, если зависимости идентичны
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Dependency other = (Dependency) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.versionRange, other.versionRange)) {
            return false;
        }
        return true;
    }

    /**
     * @return HASH код объекта
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.versionRange);
        return hash;
    }

    /**
     * @return строковое представление зависимости вида ID:Диапазон версий
     * @see VersionRange
     */
    @Override
    public String toString() {
        return id + ":" + versionRange;
    }

    /**
     * Распознает строковое представление зависимости в RSS
     *
     * @param dependencyString строка с данными зависимости
     * @return распознанное значение
     * @throws NugetFormatException ошибка формата версии
     */
    public static Dependency parseString(String dependencyString) throws NugetFormatException {
        if (!dependencyString.matches(DEPENDENCY_FORMAT)) {
            throw new NugetFormatException("Строка зависимостей не соответствует формату RSS NuGet: " + dependencyString);
        }
        Dependency dependency = new Dependency();
        String id = dependencyString.substring(0, dependencyString.indexOf(':'));
        String versionRangeString = dependencyString.substring(dependencyString.indexOf(':') + 1);
        dependency.id = id;
        dependency.versionRange = VersionRange.parse(versionRangeString);
        return dependency;
    }
    /**
     * Формат строки идентификатора пакета
     */
    private static final String PACKAGE_ID_FORMAT = "[\\w\\.\\-]+";
    /**
     * Формат строки зависимости
     */
    private static final String DEPENDENCY_FORMAT = "^" + PACKAGE_ID_FORMAT
            + ":(?:" + Version.VERSION_FORMAT + "|"
            + VersionRange.FIXED_VERSION_RANGE_PATTERN
            + "|" + VersionRange.FULL_VERSION_RANGE_PATTERN + ")?$";
}

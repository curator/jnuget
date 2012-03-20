package ru.aristar.jnuget.files;

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
public class Dependency {

    /**
     * Идентификатор пакета
     */
    @XmlAttribute(name = "id")
    public String id;
    /**
     * Версия пакета
     */
    public Version version;

    /**
     * @return строковое представление диапазона версий
     */
    @XmlAttribute(name = "version")
    public String getVersionRangeString() {
        if (version == null) {
            return null;
        }
        return version.toString();
    }

    /**
     * @param versionString строковое представление диапазона версий
     * @throws NugetFormatException некорректный формат версии
     */
    public void setVersionRangeString(String versionString) throws NugetFormatException {
        this.version = Version.parse(versionString);
    }

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
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public String toString() {
        return id + ":" + version;
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
        String id = dependencyString.substring(0, dependencyString.indexOf(":"));
        String versionString = dependencyString.substring(dependencyString.indexOf(":") + 1);
        dependency.id = id;
        dependency.version = Version.parse(versionString);
        return dependency;
    }
    /**
     * Формат строки идентификатора пакета
     */
    private static final String PACKAGE_ID_FORMAT = "[\\w\\.\\-]+";
    /**
     * Формат строки зависимости
     */
    private static final String DEPENDENCY_FORMAT = "^" + PACKAGE_ID_FORMAT + ":" + Version.VERSION_FORMAT + "$";
}

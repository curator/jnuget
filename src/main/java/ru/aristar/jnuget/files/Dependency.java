package ru.aristar.jnuget.files;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.VersionTypeAdapter;

/**
 * Описание зависимости
 *
 * @author Unlocker
 */
public class Dependency {

    /**
     * Идентификатор пакета
     */
    @XmlAttribute(name = "id")
    public String id;
    /**
     * Версия пакета
     */
    @XmlAttribute(name = "version")
    @XmlJavaTypeAdapter(value = VersionTypeAdapter.class)
    public Version version;

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
        Dependency dependency = new Dependency();
        String id = dependencyString.substring(0, dependencyString.indexOf(":"));
        String versionString = dependencyString.substring(dependencyString.indexOf(":") + 1);
        dependency.id = id;
        dependency.version = Version.parse(versionString);
        return dependency;
    }
}

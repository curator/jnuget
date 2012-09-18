package ru.aristar.jnuget.files.nuspec;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Framework;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.VersionRange;

/**
 * Описание зависимости
 *
 * @author Unlocker
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Dependency implements Serializable {

    /**
     * Логгер
     */
    private static Logger logger = LoggerFactory.getLogger(Dependency.class);
    /**
     * Идентификатор пакета
     */
    @XmlAttribute(name = "id")
    private String id;
    /**
     * Версия пакета
     */
    public VersionRange versionRange;
    /**
     * Версия фреймворка, для которой устанавливается зависимость
     */
    public Framework framework;

    /**
     * @return Идентификатор пакета
     */
    public String getId() {
        return id;
    }

    /**
     * @param id Идентификатор пакета
     */
    public void setId(String id) {
        this.id = id;
    }

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
        StringBuilder builder = new StringBuilder(128);
        builder.append(id);
        builder.append(":");
        builder.append(versionRange);
        if (framework != null) {
            builder.append(":");
            builder.append(framework.name());
        }
        return builder.toString();
    }

    /**
     * Распознает строковое представление зависимости в RSS
     *
     * @param dependencyString строка с данными зависимости
     * @return распознанное значение
     * @throws NugetFormatException ошибка формата версии
     */
    public static Dependency parseString(String dependencyString) throws NugetFormatException {
        Pattern pattern = Pattern.compile(DEPENDENCY_FORMAT);
        Matcher matcher = pattern.matcher(dependencyString);
        if (!matcher.matches()) {
            throw new NugetFormatException("Строка зависимостей не соответствует формату RSS NuGet: " + dependencyString);
        }
        Dependency dependency = new Dependency();
        String id = matcher.group("pkgId");
        String versionRangeString = matcher.group("version");
        String frameWorkString = matcher.group("frameWork");
        dependency.id = id;
        dependency.versionRange = VersionRange.parse(versionRangeString);
        if (frameWorkString != null && !frameWorkString.equals("")) {
            dependency.framework = Framework.getByShortName(frameWorkString);
            if (dependency.framework == null) {
                logger.warn("Пакет: " + id + " Не найден фреймворк " + frameWorkString);
            }
        }
        return dependency;
    }
    /**
     * Формат строки идентификатора пакета
     */
    private static final String PACKAGE_ID_FORMAT = "[\\w\\.\\-]+";
    /**
     * Формат строки зависимости
     */
    private static final String DEPENDENCY_FORMAT = "^(?<pkgId>" + PACKAGE_ID_FORMAT
            + "):(?<version>" + Version.VERSION_FORMAT + "|"
            + VersionRange.FIXED_VERSION_RANGE_PATTERN
            + "|" + VersionRange.FULL_VERSION_RANGE_PATTERN + ")?:?(?<frameWork>[^:]+)?$";
}

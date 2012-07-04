package ru.aristar.jnuget.files.nuspec;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.aristar.jnuget.files.Framework;

/**
 * Группа зависимостей
 *
 * @author sviridov
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DependenciesGroup {

    /**
     * Преобразователь списка сборок к строке с разделителями
     */
    public static class TargetFrameworkAdapter extends XmlAdapter<String, Framework> {

        @Override
        public String marshal(Framework framework) throws Exception {
            return framework.name();
        }

        @Override
        public Framework unmarshal(String farmework) throws Exception {
            if (farmework == null) {
                return null;
            }
            return Framework.valueOf(farmework);
        }
    }
    /**
     * Фреймворк для которого предназначена группа зависимостей
     */
    @XmlAttribute(name = "targetFramework")
    @XmlJavaTypeAdapter(TargetFrameworkAdapter.class)
    private Framework targetFramework;
    /**
     * Зависимости пакета
     */
    @XmlElement(name = "dependency", namespace = NuspecFile.NUSPEC_XML_NAMESPACE_2011)
    private List<Dependency> dependencys;

    /**
     * @return зависимости пакета
     */
    public List<Dependency> getDependencys() {
        if (dependencys == null) {
            dependencys = new ArrayList<>();
        }
        return dependencys;
    }

    /**
     * @param dependencys зависимости пакета
     */
    public void setDependencys(List<Dependency> dependencys) {
        this.dependencys = dependencys;
    }

    /**
     * @return фреймворк для которого предназначена группа зависимостей
     */
    public Framework getTargetFramework() {
        return targetFramework;
    }

    /**
     * @param targetFramework фреймворк для которого предназначена группа
     * зависимостей
     */
    public void setTargetFramework(Framework targetFramework) {
        this.targetFramework = targetFramework;
    }
}

package ru.aristar.jnuget.files;

import java.util.EnumSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Класс, описывающий зависимости от сборок, входящих в поставку .NET
 *
 * @author sviridov
 */
@XmlRootElement(name = "frameworkAssembly", namespace = NuspecFile.NUSPEC_XML_NAMESPACE_2011)
@XmlAccessorType(XmlAccessType.NONE)
public class FrameworkAssembly {

    /**
     * Преобразователь списка сборок к строке с разделителями
     */
    public static class AssemblyTargetFrameworkAdapter extends XmlAdapter<String, EnumSet<Framework>> {

        /**
         * Разделитель фреймворков в строке
         */
        private static final String FRAMEWORKS_DELIMETER = ", ";

        @Override
        public String marshal(EnumSet<Framework> frameworks) throws Exception {
            StringBuilder builder = new StringBuilder();
            for (Framework framework : frameworks) {
                builder.append(framework.getFullName());
                builder.append(FRAMEWORKS_DELIMETER);
            }
            return builder.substring(0, builder.length() - 3);
        }

        @Override
        public EnumSet<Framework> unmarshal(String farmeworks) throws Exception {
            if (farmeworks == null) {
                return null;
            }
            String[] names = farmeworks.split(FRAMEWORKS_DELIMETER);
            EnumSet<Framework> result = EnumSet.noneOf(Framework.class);
            for (String name : names) {
                result.add(Framework.getByFullName(name));
            }
            return result;
        }
    }
    /**
     * Название сборки
     */
    @XmlAttribute(name = "assemblyName")
    private String assemblyName;
    /**
     * Фреймворки для которых предназначена сборка
     */
    @XmlAttribute(name = "targetFramework")
    @XmlJavaTypeAdapter(AssemblyTargetFrameworkAdapter.class)
    private EnumSet<Framework> targetFrameworks;

    /**
     * @return название сборки
     */
    public String getAssemblyName() {
        return assemblyName;
    }

    /**
     * @param assemblyName название сборки
     */
    public void setAssemblyName(String assemblyName) {
        this.assemblyName = assemblyName;
    }

    /**
     * @return фреймворки для которых предназначена сборка
     */
    public EnumSet<Framework> getTargetFrameworks() {
        if (targetFrameworks == null) {
            targetFrameworks = EnumSet.allOf(Framework.class);
        }
        return targetFrameworks;
    }

    /**
     * @param targetFrameworks фреймворки для которых предназначена сборка
     */
    public void setTargetFrameworks(EnumSet<Framework> targetFrameworks) {
        this.targetFrameworks = targetFrameworks;
    }
}

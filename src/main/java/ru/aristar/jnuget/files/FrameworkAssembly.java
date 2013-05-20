package ru.aristar.jnuget.files;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import ru.aristar.jnuget.files.nuspec.NuspecFile;
import java.util.EnumSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
         * Логгер
         */
        private static Logger logger = LoggerFactory.getLogger(AssemblyTargetFrameworkAdapter.class);
        /**
         * Разделитель фреймворков в строке
         */
        private static final String FRAMEWORKS_DELIMETER = ", ";

        @Override
        public String marshal(EnumSet<Framework> frameworks) throws Exception {
            if (frameworks == null || frameworks.isEmpty()) {
                return null;
            }
            String result = Joiner.on(FRAMEWORKS_DELIMETER).join(frameworks);
            return result;
        }

        @Override
        public EnumSet<Framework> unmarshal(String farmeworks) throws Exception {
            if (Strings.isNullOrEmpty(farmeworks)) {
                return null;
            }
            String[] names = farmeworks.split(FRAMEWORKS_DELIMETER);
            EnumSet<Framework> result = EnumSet.noneOf(Framework.class);
            for (String name : names) {
                try {
                    final Framework framework = Framework.getByFullName(name);
                    if (framework != null) {
                        result.add(framework);
                    }
                } catch (Exception e) {
                    logger.warn(java.text.MessageFormat.format("Ошибка добавления фреймворка \"{0}\"", name), e);
                }
            }
            if (result.isEmpty()) {
                return null;
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

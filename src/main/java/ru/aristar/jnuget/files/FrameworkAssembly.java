package ru.aristar.jnuget.files;

import java.io.Serializable;
import ru.aristar.jnuget.files.nuspec.NuspecFile;
import java.util.EnumSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Класс, описывающий зависимости от сборок, входящих в поставку .NET
 *
 * @author sviridov
 */
@XmlRootElement(name = "frameworkAssembly", namespace = NuspecFile.NUSPEC_XML_NAMESPACE_2011)
@XmlAccessorType(XmlAccessType.NONE)
public class FrameworkAssembly implements Serializable {
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

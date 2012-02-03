package ru.aristar.jnuget.Common;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Настройки хранилища
 *
 * @author sviridov
 */
@XmlRootElement(name = "storage")
@XmlAccessorType(XmlAccessType.NONE)
public class StorageOptions {

    /**
     * Имя класса хранилища
     */
    @XmlAttribute(name = "class")
    private String className;
    @XmlJavaTypeAdapter(PropertiesTypeAdapter.class)
    @XmlElement(name = "properties")
    private Map<String, String> properties;

    /**
     * @return Имя класса хранилища
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className Имя класса хранилища
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return свойства хранилища
     */
    public Map<String, String> getProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
        return properties;
    }
}
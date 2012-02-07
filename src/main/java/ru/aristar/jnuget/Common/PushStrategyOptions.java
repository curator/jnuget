package ru.aristar.jnuget.Common;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Стратегия публикации пакетов
 *
 * @author sviridov
 */
@XmlRootElement(name = "pushStrategy")
@XmlAccessorType(XmlAccessType.NONE)
public class PushStrategyOptions {

    /**
     * Имя класса стратегии
     */
    @XmlAttribute(name = "class")
    private String className;
    @XmlJavaTypeAdapter(PropertiesTypeAdapter.class)
    @XmlElement(name = "properties")
    private Map<String, String> properties;

    /**
     * @return Имя класса стратегии
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className Имя класса стратегии
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return свойства стратегии
     */
    public Map<String, String> getProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
        return properties;
    }
}

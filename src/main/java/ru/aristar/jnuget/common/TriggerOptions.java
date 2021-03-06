package ru.aristar.jnuget.common;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Настройки триггера
 *
 * @author sviridov
 */
@XmlRootElement(name = "trigger")
@XmlAccessorType(XmlAccessType.NONE)
public class TriggerOptions {

    /**
     * Имя класса триггера
     */
    @XmlAttribute(name = "class")
    private String className;
    /**
     * Строковое значение свойств триггера
     */
    @XmlJavaTypeAdapter(PropertiesTypeAdapter.class)
    @XmlElement(name = "properties")
    private Multimap<String, String> properties;

    /**
     * @return имя класса триггера
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className имя класса триггера
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return значение свойств тгриггера
     */
    public Multimap<String, String> getProperties() {
        if (properties == null) {
            properties = HashMultimap.create();
        }
        return properties;
    }
}

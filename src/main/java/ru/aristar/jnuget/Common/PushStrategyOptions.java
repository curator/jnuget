package ru.aristar.jnuget.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    /**
     * Строковое значение свойств стратегии помещения пакета в хранилище
     */
    @XmlJavaTypeAdapter(PropertiesTypeAdapter.class)
    @XmlElement(name = "properties")
    private Map<String, String> properties;
    /**
     * Настройки триггеров, выполняющихся после вставки пакета
     */
    @XmlElementWrapper(name = "after")
    @XmlElement(name = "trigger")
    private List<TriggerOptions> aftherTriggersOptions;
    /**
     * Настройки триггеров, выполняющихся до вставки пакета
     */
    @XmlElementWrapper(name = "before")
    @XmlElement(name = "trigger")
    private List<TriggerOptions> beforeTriggersOptions;

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

    /**
     * @return настройки триггеров, выполняющихся после вставки пакета
     */
    public List<TriggerOptions> getAftherTriggersOptions() {
        if (aftherTriggersOptions == null) {
            aftherTriggersOptions = new ArrayList<>();
        }
        return aftherTriggersOptions;
    }

    /**
     * @return настройки триггеров, выполняющихся до вставки пакета
     */
    public List<TriggerOptions> getBeforeTriggersOptions() {
        if (beforeTriggersOptions == null) {
            beforeTriggersOptions = new ArrayList<>();
        }
        return beforeTriggersOptions;
    }
}

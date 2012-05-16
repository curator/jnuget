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
    /**
     * Будет ли индексироваться хранилище
     */
    @XmlAttribute(name = "indexed")
    private Boolean indexed;
    /**
     * Имя хранилища. Если задано - то индекс будет сохраняться
     */
    @XmlAttribute(name = "storageName")
    private String storageName;
    /**
     * Интервал обновления информации о хранилище в минутах. Если задан - то
     * переодически опрашивается индексируемое хранилище на предмет изменения
     * данных.
     */
    @XmlAttribute(name = "refreshInterval")
    private Integer refreshInterval;
    /**
     * Список настроек вида ключ/значение
     */
    @XmlJavaTypeAdapter(PropertiesTypeAdapter.class)
    @XmlElement(name = "properties")
    private Map<String, String> properties;
    /**
     * Стратегия публикации пакетов
     */
    @XmlElement(name = "pushStrategy")
    private PushStrategyOptions strategyOptions;

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

    /**
     * @return Стратегия публикации пакетов
     */
    public PushStrategyOptions getStrategyOptions() {
        return strategyOptions;
    }

    /**
     * @param strategyOptions Стратегия публикации пакетов
     */
    public void setStrategyOptions(PushStrategyOptions strategyOptions) {
        this.strategyOptions = strategyOptions;
    }

    /**
     * @return будет ли индексироваться хранилище
     */
    public boolean isIndexed() {
        if (indexed == null) {
            indexed = true;
        }
        return indexed;
    }

    /**
     * @param indexed будет ли индексироваться хранилище
     */
    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    /**
     * @return Интервал обновления информации о хранилище в минутах.
     */
    public Integer getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * @param refreshInterval Интервал обновления информации о хранилище в
     * минутах.
     */
    public void setRefreshInterval(Integer refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * @return Имя хранилища
     */
    public String getStorageName() {
        return storageName;
    }

    /**
     * @param storageName Имя хранилища
     */
    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }
}

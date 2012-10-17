package ru.aristar.jnuget.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * Разрешена ли публикация пакетов в хранилище
     */
    @XmlAttribute(name = "canPush")
    private Boolean canPush;
    /**
     * Разрешено ли удаление пакетов из хранилища
     */
    @XmlAttribute(name = "canDelete")
    private Boolean canDelete;
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
     * Строка настройки планировщика cron
     */
    @XmlAttribute(name = "schedule")
    private String cronString;
    /**
     * Список настроек вида ключ/значение
     */
    @XmlJavaTypeAdapter(PropertiesTypeAdapter.class)
    @XmlElement(name = "properties")
    private Map<String, String> properties;
    /**
     * Настройки триггеров, выполняющихся после вставки пакета
     */
    @XmlElementWrapper(name = "afterTriggers")
    @XmlElement(name = "trigger")
    private List<TriggerOptions> aftherTriggersOptions;
    /**
     * Настройки триггеров, выполняющихся до вставки пакета
     */
    @XmlElementWrapper(name = "beforeTriggers")
    @XmlElement(name = "trigger")
    private List<TriggerOptions> beforeTriggersOptions;

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
     * @return будет ли индексироваться хранилище
     */
    public boolean isIndexed() {
        if (indexed == null) {
            indexed = true;
        }
        return indexed;
    }

    /**
     * @return Разрешена ли публикация пакетов в хранилище
     */
    public boolean isCanPush() {
        if (canPush == null) {
            canPush = false;
        }
        return canPush;
    }

    /**
     * @return разрешено ли удаление пакетов из хранилища
     */
    public Boolean isCanDelete() {
        if (canDelete == null) {
            canDelete = false;
        }
        return canDelete;
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

    /**
     * @return строка настройки планировщика cron
     */
    public String getCronString() {
        return cronString;
    }

    /**
     * @param cronString строка настройки планировщика cron
     */
    public void setCronString(String cronString) {
        this.cronString = cronString;
    }
}

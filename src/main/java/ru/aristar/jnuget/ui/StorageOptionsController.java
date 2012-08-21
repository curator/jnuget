package ru.aristar.jnuget.ui;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static java.text.MessageFormat.format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import ru.aristar.jnuget.common.Options;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.IndexedPackageSource;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;
import ru.aristar.jnuget.sources.push.AfterTrigger;
import ru.aristar.jnuget.sources.push.BeforeTrigger;
import ru.aristar.jnuget.sources.push.ModifyStrategy;
import ru.aristar.jnuget.ui.descriptors.DescriptorsFactory;
import ru.aristar.jnuget.ui.descriptors.ObjectDescriptor;
import ru.aristar.jnuget.ui.descriptors.ObjectProperty;

/**
 * Контроллер настроек хранилища
 *
 * @author sviridov
 */
@ManagedBean(name = "storageOptions")
@RequestScoped
public class StorageOptionsController implements Serializable {

    /**
     * Свойства объекта
     */
    public class Properties {

        /**
         * Имя объекта
         */
        private String objectName;
        /**
         * Свойства объекта
         */
        private List<Property> properties;

        /**
         * @param objectName имя объекта
         */
        public Properties(String objectName) {
            this.objectName = objectName;
        }

        /**
         * @return имя объекта
         */
        public String getObjectName() {
            return objectName;
        }

        /**
         * @return свойства объекта
         */
        public List<Property> getProperties() {
            if (properties == null) {
                properties = new ArrayList<>();
            }
            return properties;
        }
    }

    /**
     * Свойсво объекта
     */
    public class Property {

        /**
         * Уникальное имя свойства
         */
        private String name;
        /**
         * Описание свойства
         */
        private String description;
        /**
         * Строковое представление значения
         */
        private String value;

        /**
         * @return уникальное имя свойства
         */
        public String getName() {
            return name;
        }

        /**
         * @param name уникальное имя свойства
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return описание свойства
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description описание свойства
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return строковое представление значения
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value строковое представление значения
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * @param name уникальное имя свойства
         * @param description описание свойства
         * @param value строковое представление значения
         */
        public Property(String name, String description, String value) {
            this.name = name;
            this.description = description;
            this.value = value;
        }
    }
    /**
     * Идентификатор хранилища
     */
    private Integer storageId;
    /**
     * Хранилище
     */
    private PackageSource<? extends Nupkg> packageSource;
    /**
     * Индексирующий декоратор (или null, если хранилище не индексируется)
     */
    private IndexedPackageSource indexDecorator;

    /**
     * Инициализация хранилища
     */
    private void init() {
        if (storageId == null) {
            packageSource = null;
            indexDecorator = null;
            return;
        }
        packageSource = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        if (packageSource instanceof IndexedPackageSource) {
            indexDecorator = (IndexedPackageSource) packageSource;
            packageSource = indexDecorator.getUnderlyingSource();
        }
    }

    /**
     * @return идентификатор хранилища
     */
    public Integer getStorageId() {
        return storageId;
    }

    /**
     * @param storageId идентификатор хранилища
     */
    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
        init();
    }

    /**
     * @return имя класса хранилища
     */
    public String getClassName() {
        return packageSource == null ? null : packageSource.getClass().getCanonicalName();
    }

    /**
     * @param className имя класса хранилища
     * @throws NugetFormatException ошибка создания хранилища указанного класса
     */
    @SuppressWarnings("unchecked")
    public void setClassName(String className) throws
            NugetFormatException {
        try {
            Class<?> packageSourceClass = Class.forName(className);
            Constructor<?> constructor = packageSourceClass.getConstructor();
            Object result = constructor.newInstance();
            if (result instanceof PackageSource) {
                packageSource = (PackageSource<Nupkg>) result;
            } else {
                throw new NugetFormatException(format("Класс {0} не является {1}", className, PackageSource.class.getName()));
            }
        } catch (NoSuchMethodException | ClassNotFoundException |
                InstantiationException |
                IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e) {
            throw new NugetFormatException(format("Ошибка создания объекта класса {0}", className), e);
        }
    }

    /**
     * @return индексируемое или нет хранилище
     */
    public boolean isIndexed() {
        return indexDecorator != null;
    }

    /**
     * @param value индексируемое или нет хранилище
     */
    public void setIndexed(boolean value) {
        indexDecorator = new IndexedPackageSource();
        indexDecorator.setUnderlyingSource(packageSource);
    }

    /**
     * @return интервал обновления индекса хранилища
     */
    public Integer getRefreshInterval() {
        if (indexDecorator == null) {
            return null;
        }
        return indexDecorator.getRefreshInterval();
    }

    /**
     * @param value интервал обновления индекса хранилища
     */
    public void setRefreshInterval(Integer value) {
        if (indexDecorator == null) {
            return;
        }
        indexDecorator.setRefreshInterval(value);
    }

    /**
     * @param storageName имя хранилища (используется для сохранения индекса)
     */
    public void setStorageName(String storageName) {
        if (indexDecorator == null) {
            return;
        }
        File storageFile = IndexedPackageSource.getIndexSaveFile(Options.getNugetHome(), storageName);
        indexDecorator.setIndexStoreFile(storageFile);
    }

    /**
     * @return имя хранилища (используется для сохранения индекса)
     */
    public String getStorageName() {
        if (indexDecorator == null) {
            return null;
        }
        File indexFile = indexDecorator.getIndexStoreFile();
        if (indexFile == null) {
            return null;
        }
        String fileName = indexDecorator.getIndexStoreFile().getName();
        fileName = fileName.substring(0, fileName.length() - 4);
        return fileName;
    }

    /**
     * @return триггеры, выполняющиеся после вставки пакета
     */
    public List<Properties> getAftherTriggers() {
        List<Properties> triggerOptions = new ArrayList<>();
        if (packageSource != null && packageSource.getPushStrategy() != null) {
            ModifyStrategy pushStrategy = packageSource.getPushStrategy();
            final List<AfterTrigger> afterTriggers = pushStrategy.getAftherPushTriggers();
            for (int i = 0; i < afterTriggers.size(); i++) {
                AfterTrigger afterTrigger = afterTriggers.get(i);
                Properties properties = new Properties(format("Триггер {0}", i + 1));
                triggerOptions.add(properties);
                Property classProperty = new Property("triggerClass", "Имя класса триггера", afterTrigger.getClass().getName());
                properties.getProperties().add(classProperty);
                properties.getProperties().addAll(getObjectProperties(afterTrigger));
            }
        }
        return triggerOptions;
    }

    /**
     * @return триггеры, выполняющиеся перед вставкой пакета
     */
    public List<Properties> getBeforeTriggers() {
        List<Properties> triggerOtions = new ArrayList<>();
        if (packageSource != null && packageSource.getPushStrategy() != null) {
            ModifyStrategy pushStrategy = packageSource.getPushStrategy();
            final List<BeforeTrigger> beforeTriggers = pushStrategy.getBeforePushTriggers();
            for (int i = 0; i < beforeTriggers.size(); i++) {
                BeforeTrigger beforeTrigger = beforeTriggers.get(i);
                Properties properties = new Properties(format("Триггер {}", i + 1));
                triggerOtions.add(properties);
                Property classProperty = new Property("triggerClass", "Имя класса триггера", beforeTrigger.getClass().getName());
                properties.getProperties().add(classProperty);
                properties.getProperties().addAll(getObjectProperties(beforeTrigger));
            }
        }
        return triggerOtions;
    }

    /**
     * @return параметры настройки хранилища
     */
    public Collection<Property> getStorageProperties() {
        ArrayList<Property> data = new ArrayList<>();
        if (packageSource != null) {
            data.addAll(getObjectProperties(packageSource));
        }
        return data;
    }

    /**
     * @return разрешена или нет публикация
     */
    public boolean getCanPush() {
        if (packageSource == null || packageSource.getPushStrategy() == null) {
            return false;
        }
        return packageSource.getPushStrategy().canPush();
    }

    /**
     * @return разрешено или нет удаление
     */
    public boolean getCanDelete() {
        if (packageSource == null || packageSource.getPushStrategy() == null) {
            return false;
        }
        return packageSource.getPushStrategy().canDelete();

    }

    /**
     * Получение свойств объекта
     *
     * @param object объект, для которого необходимо получить список свойств
     * @return список свойств
     */
    protected ArrayList<Property> getObjectProperties(Object object) {
        ArrayList<Property> propertys = new ArrayList<>();
        ObjectDescriptor<?> descriptor = DescriptorsFactory.getInstance().getObjectDescriptor(object.getClass());
        if (descriptor == null) {
            return propertys;
        }
        for (ObjectProperty property : descriptor.getProperties()) {
            final String description = property.getDescription();
            final String value = property.getValue(object);
            final String name = property.getName();
            Property result = new Property(name, description, value);
            propertys.add(result);
        }
        return propertys;
    }
}

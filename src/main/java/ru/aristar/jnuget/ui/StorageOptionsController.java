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
import javax.faces.model.ListDataModel;
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
    public List<AfterTrigger> getAftherTriggers() {
        ArrayList<AfterTrigger> triggers = new ArrayList<>();
        if (packageSource != null && packageSource.getPushStrategy() != null) {
            ModifyStrategy pushStrategy = packageSource.getPushStrategy();
            triggers.addAll(pushStrategy.getAftherPushTriggers());
        }
        return triggers;
    }

    /**
     * @return триггеры, выполняющиеся перед вставкой пакета
     */
    public List<BeforeTrigger> getBeforeTriggers() {
        ArrayList<BeforeTrigger> triggers = new ArrayList<>();
        if (packageSource != null && packageSource.getPushStrategy() != null) {
            ModifyStrategy pushStrategy = packageSource.getPushStrategy();
            triggers.addAll(pushStrategy.getBeforePushTriggers());
        }
        return triggers;
    }

    /**
     * @return параметры настройки хранилища
     */
    public Collection<Property> getStorageProperties() {
        ArrayList<Property> data = new ArrayList<>();
        if (packageSource != null) {
            ObjectDescriptor<? extends PackageSource> descriptor = DescriptorsFactory.getInstance().getPackageSourceDescriptor(packageSource.getClass());
            if (descriptor != null) {
                for (ObjectProperty property : descriptor.getProperties()) {
                    final String description = property.getDescription();
                    final String value = property.getValue(packageSource);
                    final String name = property.getName();
                    Property result = new Property(name, description, value);
                    data.add(result);
                }
            }
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
}

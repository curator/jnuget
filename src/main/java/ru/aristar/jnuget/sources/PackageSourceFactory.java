package ru.aristar.jnuget.sources;

import com.google.common.collect.Multimap;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.activation.UnsupportedDataTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.common.CollectionGenericType;
import ru.aristar.jnuget.common.OptionConverter;
import ru.aristar.jnuget.common.Options;
import ru.aristar.jnuget.common.StorageOptions;
import ru.aristar.jnuget.common.TriggerOptions;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.AfterTrigger;
import ru.aristar.jnuget.sources.push.BeforeTrigger;
import ru.aristar.jnuget.sources.push.ModifyStrategy;

/**
 * Фабрика источников данных
 *
 * @author sviridov
 */
public class PackageSourceFactory {

    //TODO Переписать ифабрику под использование ObjectDescriptor
    /**
     * Экземпляр фабрики
     */
    protected volatile static PackageSourceFactory instance;
    /**
     * Настройки сервера
     */
    private final Options options;
    /**
     * Активные хранилища пакетов
     */
    private volatile ConcurrentHashMap<String, PackageSource<Nupkg>> packageSources;
    /**
     * Активные хранилища пакетов
     */
    private volatile ConcurrentHashMap<String, PackageSource<Nupkg>> publicPackageSources;

    /**
     * Конструктор, перечитывающий настройки
     */
    public PackageSourceFactory() {
        this.options = Options.loadOptions();
    }
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Создает индексируемую обертку для хранилища пакетов
     *
     * @param packageSource хранилище, которое необходимо индексировать
     * @param storageName имя, используемое для сохранения индекса
     * @param refreshInterval интервал обновления информации в индекск
     * @param cronString строка cron (планирование обновления индекса)
     * @param saveIndex сохранять или нет индекс на диске
     * @return индексируемое хранилище
     */
    protected PackageSource<Nupkg> createIndexForStorage(PackageSource<Nupkg> packageSource,
            String storageName, Integer refreshInterval, String cronString, boolean saveIndex) {
        logger.debug("Создание индекса для хранилища {}", new Object[]{packageSource});
        IndexedPackageSource indexedPackageSource = new IndexedPackageSource();
        indexedPackageSource.setUnderlyingSource(packageSource);
        if (saveIndex) {
            File storageFile = IndexedPackageSource.getIndexSaveFile(Options.getNugetHome(), storageName);
            indexedPackageSource.setIndexStoreFile(storageFile);
        }
        if (cronString != null) {
            logger.info("Расписание обновления индекса для хранилища {} установлено в \"{}\"",
                    new Object[]{packageSource, cronString});
            indexedPackageSource.setCronSheduller(cronString);
        } else if (refreshInterval != null) {
            logger.info("Интервал обновления для хранилища {} установлен в {}",
                    new Object[]{packageSource, refreshInterval});
            indexedPackageSource.setRefreshInterval(refreshInterval);
        }
        return indexedPackageSource;
    }

    /**
     * Создание хранилищ на основе настроек
     *
     * @param serviceOptions настройки приложения
     */
    protected void createPackageSources(Options serviceOptions) {
        //Создание корневого хранилища
        logger.info("Инициализация файлового хранища");
        packageSources = new ConcurrentHashMap<>();
        publicPackageSources = new ConcurrentHashMap<>();
        for (StorageOptions storageOptions : serviceOptions.getStorageOptionsList()) {
            try {
                if (storageOptions.getStorageName() == null) {
                    throw new IllegalArgumentException("Имя хранилища должно быть указано");
                }
                PackageSource<Nupkg> childSource = createPackageSource(storageOptions);
                packageSources.put(storageOptions.getStorageName(), childSource);
                if (storageOptions.isPublic()) {
                    publicPackageSources.put(storageOptions.getStorageName(), childSource);
                }
            } catch (Exception e) {
                logger.warn("Ошибка создания хранилища пакетов", e);
            }
        }
        logger.info("Создано {} хранилищ из них публичных {}", new Object[]{packageSources.size(), publicPackageSources.size()});
    }

    /**
     * Производит поиск сеттера для свойства
     *
     * @param sourceClass класс, в котором производится поиск сеттера
     * @param propertyName имя свойства
     * @return метод - сеттер
     * @throws NoSuchMethodException метод не найден
     */
    protected Method findSetter(Class<?> sourceClass, String propertyName) throws NoSuchMethodException {
        String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        for (Method method : sourceClass.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        throw new NoSuchMethodException("Метод " + setterName + " не найден в классе " + sourceClass.getName());
    }

    /**
     * Создание нового дочернего хранилища на основе настроек
     *
     * @param storageOptions настройки хранилища
     * @return хранилище пакетов
     * @throws Exception ошибка создания хранилища
     */
    protected PackageSource<Nupkg> createPackageSource(StorageOptions storageOptions)
            throws Exception {
        //Создание файлового хранилища
        logger.info("Инициализация хранилища типа {}", new Object[]{storageOptions.getClassName()});
        Class<?> sourceClass = Class.forName(storageOptions.getClassName());
        Object object = sourceClass.newInstance();
        if (!(object instanceof PackageSource)) {
            throw new UnsupportedDataTypeException("Класс " + sourceClass + " не является хранилищем пакетов");
        }
        @SuppressWarnings("unchecked")
        PackageSource<Nupkg> newSource = (PackageSource) object;
        newSource.setName(storageOptions.getStorageName());
        setObjectProperties(storageOptions.getProperties(), object);
        ModifyStrategy pushStrategy = createPushStrategy(storageOptions);
        newSource.setPushStrategy(pushStrategy);
        logger.info("Установлена стратегия фиксации");
        if (storageOptions.isIndexed()) {
            newSource = createIndexForStorage(
                    newSource,
                    storageOptions.getStorageName(),
                    storageOptions.getRefreshInterval(),
                    storageOptions.getCronString(),
                    storageOptions.isSaveIndex());
        }
        logger.info("Хранилище создано");
        return newSource;
    }

    /**
     * Создает стратегию фиксации пакетов
     *
     * @param storegeOptions настройки стратегии
     * @return стратегия фиксации
     * @throws Exception ошибка создания стратегии
     */
    protected ModifyStrategy createPushStrategy(StorageOptions storegeOptions) throws Exception {
        //Создание стратегии фиксации
        //TODO убрать настройки стратегии, оставить только разрешение/запрещение публикации
        logger.info("Инициализация стратегии фиксации и удаления");
        ModifyStrategy pushStrategy = new ModifyStrategy(storegeOptions.isCanPush(), storegeOptions.isCanDelete());
        //Триггеры BEFORE
        pushStrategy.getBeforePushTriggers().addAll(createTriggers(storegeOptions.getBeforeTriggersOptions(), BeforeTrigger.class));
        //Триггеры Afther
        pushStrategy.getAftherPushTriggers().addAll(createTriggers(storegeOptions.getAftherTriggersOptions(), AfterTrigger.class));
        logger.info("Стратегия создана");
        return pushStrategy;
    }

    /**
     * Создает коллекцию триггеров из коллекции настроек
     *
     * @param <T> класс требуемого объекта
     * @param options коллекция настроек
     * @param triggerClass класс требуемого объекта
     * @return коллекция настроек
     * @throws Exception ошибка создания триггера
     */
    protected <T> Collection<T> createTriggers(Collection<TriggerOptions> options, Class<T> triggerClass) throws Exception {
        ArrayList<T> pushTriggers = new ArrayList<>();
        for (TriggerOptions triggerOptions : options) {
            T pushTrigger = createTrigger(triggerOptions, triggerClass);
            pushTriggers.add(pushTrigger);
        }
        return pushTriggers;
    }

    /**
     * Создает триггер фиксации пакета
     *
     * @param <T> класс требуемого объекта
     * @param triggerOptions настройки триггера
     * @param triggerClass класс требуемого объекта
     * @return триггер
     * @throws Exception ошибка создания триггера
     */
    protected <T> T createTrigger(TriggerOptions triggerOptions, Class<T> triggerClass) throws Exception {
        logger.info("Создание триггера типа {}", new Object[]{triggerOptions.getClassName()});
        Class<?> sourceClass = Class.forName(triggerOptions.getClassName());
        if (!triggerClass.isAssignableFrom(sourceClass)) {
            throw new UnsupportedDataTypeException("Класс " + sourceClass
                    + " не является " + BeforeTrigger.class.getCanonicalName());
        }
        Object object = sourceClass.newInstance();
        @SuppressWarnings("unchecked")
        T trigger = (T) object;
        setObjectProperties(triggerOptions.getProperties(), trigger);
        logger.info("Триггер создан");
        return trigger;
    }

    /**
     * Устанавливает свойства объекту
     *
     * @param properties карта свойств
     * @param newObject объект
     * @throws Exception ошибка установки свойств
     */
    private void setObjectProperties(Multimap<String, String> properties, Object newObject)
            throws Exception {
        Class<?> sourceClass = newObject.getClass();
        for (String key : properties.keySet()) {
            Method method = findSetter(sourceClass, key);
            Class<?> valueType = method.getParameterTypes()[0];
            Object value;
            if (Collection.class.isAssignableFrom(valueType)) {
                value = getCollectionValue(method, valueType, properties.get(key));
            } else {
                value = getSingleValue(valueType, properties.get(key));
            }
            method.invoke(newObject, value);

        }
    }

    /**
     * Возвращает экземпляр фабрики, или создает новый
     *
     * @return экземпляр фабрики
     */
    public static PackageSourceFactory getInstance() {
        if (instance == null) {
            synchronized (PackageSourceFactory.class) {
                if (instance == null) {
                    instance = new PackageSourceFactory();
                }
            }
        }
        return instance;
    }

    /**
     * @return хранилища пакетов
     */
    private Map<String, PackageSource<Nupkg>> getPackageSourcesMap() {
        if (packageSources == null) {
            synchronized (this) {
                if (packageSources == null) {
                    createPackageSources(options);
                }
            }
        }
        return packageSources;
    }

    /**
     *
     * @return публичные хранилища пакетов
     */
    private Map<String, PackageSource<Nupkg>> getPublicPackageSourcesMap() {
        if (publicPackageSources == null) {
            synchronized (this) {
                if (publicPackageSources == null) {
                    createPackageSources(options);
                }
            }
        }
        return publicPackageSources;
    }

    /**
     * @return источники пакетов
     */
    public List<PackageSource<Nupkg>> getPackageSources() {
        ArrayList<PackageSource<Nupkg>> result = new ArrayList<>(getPackageSourcesMap().values());
        return result;
    }

    /**
     * @return публичное доступные источники пакетов
     */
    public List<PackageSource<Nupkg>> getPublicPackageSources() {
        ArrayList<PackageSource<Nupkg>> result = new ArrayList<>(getPublicPackageSourcesMap().values());
        return result;
    }

    /**
     * @param storageName имя хранилища
     * @return хранилище пакетов или null
     */
    public PackageSource<Nupkg> getPackageSource(String storageName) {
        return getPackageSourcesMap().get(storageName);
    }

    /**
     * @param storageName имя хранилища
     * @return публичное хранилище пакетов или null
     */
    public PackageSource<Nupkg> getPublicPackageSource(String storageName) {
        return getPublicPackageSourcesMap().get(storageName);
    }

    /**
     * @return настройки приложения
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Производит попытку создать значение указанного типа из строки
     *
     * @param <T> тип значения
     * @param valueType тип значения
     * @param stringValue строковое представление типа значения
     * @return распознанное значение
     * @throws InstantiationException ошибка вызова конструктора
     * @throws NoSuchMethodException не найден конструктор, принимающий как
     * аргумент строку
     * @throws IllegalArgumentException не найден конструктор, принимающий как
     * аргумент строку
     * @throws InvocationTargetException ошибка вызова конструктора
     * @throws SecurityException конструктор с указанными параметрами не
     * является публичным
     * @throws IllegalAccessException ошибка вызова конструктора
     */
    public static <T> T getValueFromString(Class<T> valueType, String stringValue) throws
            InstantiationException,
            NoSuchMethodException,
            IllegalArgumentException,
            InvocationTargetException,
            SecurityException,
            IllegalAccessException {
        T value;
        if (valueType.isPrimitive()) {
            value = getPrimitiveValue(stringValue, valueType);
        } else {
            final Constructor<T> constructor = valueType.getConstructor(String.class);
            value = constructor.newInstance(stringValue);
        }
        return value;
    }

    /**
     * Возвращает значение для примитивного типа
     *
     * @param <T> тип значения
     * @param string строковое значение
     * @param targetClass тип значения, в которое требуется преобразовать строку
     * @return преобразованное значение примитивного типа
     */
    @SuppressWarnings("unchecked")
    private static <T> T getPrimitiveValue(String string, Class<T> targetClass) {
        if (targetClass == java.lang.Boolean.TYPE) {
            return (T) Boolean.valueOf(string);
        } else if (targetClass == java.lang.Character.TYPE) {
            return (T) Character.valueOf(string.charAt(0));
        } else if (targetClass == java.lang.Byte.TYPE) {
            return (T) Byte.valueOf(string);
        } else if (targetClass == java.lang.Short.TYPE) {
            return (T) Short.valueOf(string);
        } else if (targetClass == java.lang.Integer.TYPE) {
            return (T) Integer.valueOf(string);
        } else if (targetClass == java.lang.Long.TYPE) {
            return (T) Long.valueOf(string);
        } else if (targetClass == java.lang.Float.TYPE) {
            return (T) Float.valueOf(string);
        } else if (targetClass == java.lang.Double.TYPE) {
            return (T) Double.valueOf(string);
        } else {
            throw new UnsupportedOperationException("Primitive type "
                    + targetClass + " is unsupported");
        }
    }

    /**
     * Получение значения, если параметр является коллекцией
     *
     * @param method метод сеттер.
     * @param valueType тип коллекции.
     * @param values строковые представления значений коллекции.
     * @return коллекция значений.
     * @throws Exception ошибка преобразования.
     */
    private Collection getCollectionValue(Method method, Class<?> valueType, Collection<String> values) throws Exception {
        CollectionGenericType annotation = method.getAnnotation(CollectionGenericType.class);
        Class<?> elementType = annotation == null ? String.class : annotation.type();
        @SuppressWarnings("unchecked")
        Collection<? super Object> result = (Collection) valueType.getConstructor().newInstance();
        for (String stringValue : values) {
            stringValue = OptionConverter.replaceVariables(stringValue);
            Object value = getValueFromString(elementType, stringValue);
            result.add(value);
        }
        return result;
    }

    /**
     * Получение значения, для простого параметра (не коллекции)
     *
     * @param valueType тип параметра
     * @param values строковое значение
     * @return значение
     * @throws Exception ошибка преобразования.
     */
    private Object getSingleValue(Class<?> valueType, Collection<String> values) throws Exception {
        Object value;
        String stringValue = values.iterator().next();
        stringValue = OptionConverter.replaceVariables(stringValue);
        value = getValueFromString(valueType, stringValue);
        return value;
    }
}

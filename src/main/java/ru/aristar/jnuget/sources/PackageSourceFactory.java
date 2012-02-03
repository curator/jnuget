package ru.aristar.jnuget.sources;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Common.OptionConverter;
import ru.aristar.jnuget.Common.Options;
import ru.aristar.jnuget.Common.StorageOptions;

/**
 * Фабрика источников данных
 *
 * @author sviridov
 */
public class PackageSourceFactory {

    /**
     * Экземпляр фабрики
     */
    private volatile static PackageSourceFactory instance;
    /**
     * Настройки сервера
     */
    private final Options options;

    /**
     * Конструктор, перечитывающий настройки
     */
    public PackageSourceFactory() {
        this.options = Options.loadOptions();
    }
    /**
     * Источник пакетов
     */
    private volatile PackageSource packageSource = null;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Создание нового корневого хранилища на основе настроек
     *
     * @param serviceOptions настройки приложения
     * @return хранилище пакетов
     */
    protected PackageSource createRootPackageSource(Options serviceOptions) {
        //Создание корневого хранилища
        logger.info("Инициализация файлового хранища");
        RootPackageSource rootPackageSource = new RootPackageSource();
        //TODO Стратегию сделать персональной для каждого хранилища
        if (serviceOptions.getApiKey() != null) {
            rootPackageSource.setPushStrategy(new ApiKeyPushStrategy(serviceOptions.getApiKey()));
            logger.info("Установлен ключ для фиксации пакетов");
        } else {
            rootPackageSource.setPushStrategy(new SimplePushStrategy(false));
            logger.warn("Используется стратегия фиксации по умолчанию");
        }

        for (StorageOptions storageOptions : serviceOptions.getStorageOptionsList()) {
            try {
                PackageSource childSource = createPackageSource(storageOptions);
                rootPackageSource.getSources().add(childSource);
            } catch (Exception e) {
                logger.warn("Ошибка создания хранилища пакетов", e);
            }
        }
        return rootPackageSource;
    }

    /**
     * Производит поиск сеттера для свойства
     *
     * @param sourceClass класс, в котором производится поиск сеттера
     * @param propertyName имя свойства
     * @return метод - сеттер
     * @throws NoSuchMethodException метод не найден
     */
    protected Method findSetter(Class<? extends PackageSource> sourceClass, String propertyName) throws NoSuchMethodException {
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
    protected PackageSource createPackageSource(StorageOptions storageOptions)
            throws Exception {
        //Создание файлового хранилища
        logger.info("Инициализация хранилища типа {}", new Object[]{storageOptions.getClassName()});
        Class<? extends PackageSource> sourceClass = (Class<? extends PackageSource>) Class.forName(storageOptions.getClassName());
        PackageSource newSource = sourceClass.newInstance();
        for (Map.Entry<String, String> entry : storageOptions.getProperties().entrySet()) {
            Method method = findSetter(sourceClass, entry.getKey());
            Class<?> valueType = method.getParameterTypes()[0];
            String stringValue = OptionConverter.replaceVariables(entry.getValue());
            Object value = valueType.getConstructor(String.class).newInstance(stringValue);
            method.invoke(newSource, value);
        }
        logger.info("Хранилище создано");
        return newSource;
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
     * Возвращает источник пакетов
     *
     * @return источник пакетов
     */
    public PackageSource getPackageSource() {
        if (packageSource == null) {
            //TODO Добавить возможность переинициализации
            synchronized (this) {
                if (packageSource == null) {
                    packageSource = createRootPackageSource(options);
                }
            }
        }
        return packageSource;
    }
}

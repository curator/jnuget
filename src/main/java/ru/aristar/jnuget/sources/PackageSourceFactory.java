package ru.aristar.jnuget.sources;

import java.io.File;
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
            PackageSource childSource = createPackageSource(storageOptions);
            rootPackageSource.getSources().add(childSource);
        }
        return rootPackageSource;
    }

    /**
     * Создание нового дочернего хранилища на основе настроек
     *
     * @param storageOptions настройки хранилища
     * @return хранилище пакетов
     */
    protected PackageSource createPackageSource(StorageOptions storageOptions) {
        //Создание файлового хранилища
        //TODO Заменить заглушку на нормальную реализацию
        String folderName = OptionConverter.replaceVariables(storageOptions.getProperties().get("folderName"));
        File file = new File(folderName);
        FilePackageSource filePackageSource = new FilePackageSource(file);
        logger.info("Создано файловое хранилище с адресом: {}", new Object[]{file});
        return filePackageSource;
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

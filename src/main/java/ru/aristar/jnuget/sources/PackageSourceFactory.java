package ru.aristar.jnuget.sources;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Common.OptionConverter;
import ru.aristar.jnuget.Common.Options;

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
     * Создание нового хранилища на основе настроек
     *
     * @param sourceOptions 
     * @return хранилище пакетов
     */
    protected PackageSource createPackageSource(Options sourceOptions) {
        //Создание корневого хранилища
        logger.info("Инициализация файлового хранища");
        RootPackageSource rootPackageSource = new RootPackageSource();
        rootPackageSource.setPushStrategy(new SimplePushStrategy(true));

        //Создание файлового хранилища
        String folderName = OptionConverter.replaceVariables(sourceOptions.getFolderName());
        File file = new File(folderName);
        FilePackageSource childSource = new FilePackageSource(file);
        logger.info("Создано файловое хранилище с адресом: {}", new Object[]{file});
        if (sourceOptions.getApiKey() != null) {
            childSource.setPushStrategy(new ApiKeyPushStrategy(sourceOptions.getApiKey()));
            logger.info("Установлен ключ для фиксации пакетов");
        } else {
            childSource.setPushStrategy(new SimplePushStrategy(false));
            logger.warn("Используется стратегия фиксации по умолчанию");
        }
        rootPackageSource.getSources().add(childSource);

        return rootPackageSource;
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
                    packageSource = createPackageSource(options);
                }
            }
        }
        return packageSource;
    }
}

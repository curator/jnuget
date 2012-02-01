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
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
        //Создание корневого хранилища
        RootPackageSource rootPackageSource = new RootPackageSource();
        rootPackageSource.setPushStrategy(new SimplePushStrategy(true));

        //Создание файлового хранилища
        String folderName = OptionConverter.replaceVariables(options.getFolderName());
        File file = new File(folderName);
        FilePackageSource packageSource = new FilePackageSource(file);
        logger.info("Создано файловое хранилище с адресом: {}", new Object[]{file});
        if (options.getApiKey() != null) {
            packageSource.setPushStrategy(new ApiKeyPushStrategy(options.getApiKey()));
            logger.info("Установлен ключ для фиксации пакетов");
        } else {
            packageSource.setPushStrategy(new SimplePushStrategy(false));
            logger.warn("Используется стратегия фиксации по умолчанию");
        }
        rootPackageSource.getSources().add(packageSource);

        return rootPackageSource;
    }
}

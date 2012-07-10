package ru.aristar.jnuget.ui.descriptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import static java.text.MessageFormat.format;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class DescriptorsFactory {

    /**
     * Описания хранилищ по классам, которые они описывают
     */
    private volatile Map<Class<? extends PackageSource>, PackageSourceDescriptor> sourceDescriptorsMap;
    /**
     * Логгер
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @return описания хранилищ по классам, которые они описывают
     */
    private Map<Class<? extends PackageSource>, PackageSourceDescriptor> getDescriptorMap() {
        if (sourceDescriptorsMap == null) {
            synchronized (this) {
                if (sourceDescriptorsMap == null) {
                    sourceDescriptorsMap = loadDescriptors();
                }
            }
        }
        return sourceDescriptorsMap;
    }

    /**
     * Загружает все дескрипторы, указанные в файле по данному URL
     *
     * @param url url файла с дескрипторами
     * @return коллекция дескрипторов
     */
    private Collection<PackageSourceDescriptor> loadDescriptors(URL url) {
        ArrayList<PackageSourceDescriptor> result = new ArrayList<>();
        try {
            File file = new File(url.toURI());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                String className = bufferedReader.readLine();
                try {
                    Constructor<?> constructor = Class.forName(className).getConstructor();
                    Object o = constructor.newInstance();
                    if (o instanceof PackageSourceDescriptor) {
                        result.add((PackageSourceDescriptor) o);
                    } else {
                        logger.error(format("Класс {0} не является PackageSourceDescriptor", className));
                    }
                } catch (ClassNotFoundException | NoSuchMethodException |
                        SecurityException | InstantiationException |
                        IllegalAccessException | IllegalArgumentException |
                        InvocationTargetException e) {
                    logger.error(format("Ошибка создания дескриптора типа {0}", className), e);
                }
            }
        } catch (URISyntaxException | IOException e) {
            logger.error(format("Ошибка чтения списка дескрипторов для URL {0}", url), e);
        }
        return result;
    }

    /**
     * Загружает все доступные приложению дескрипторы
     *
     * @return описания хранилищ по классам, которые они описывают
     */
    private Map<Class<? extends PackageSource>, PackageSourceDescriptor> loadDescriptors() {
        Map<Class<? extends PackageSource>, PackageSourceDescriptor> result = new HashMap<>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("ru/aristar/jnuget/sources/storageDescriptors.list");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Collection<PackageSourceDescriptor> descriptors = loadDescriptors(url);
                for (PackageSourceDescriptor descriptor : descriptors) {
                    result.put(descriptor.getPackageSourceClass(), descriptor);
                }
            }
        } catch (IOException e) {
            logger.error("Не удалось загрузить описания источников пакетов", e);
        }
        return result;
    }

    /**
     * Возвращает описание хранилища
     *
     * @param c класс хранилища
     * @return описание хранилища
     */
    public PackageSourceDescriptor getPackageSourceDescriptor(Class<? extends PackageSource> c) {
        return getDescriptorMap().get(c);
    }

    /**
     * @return все доступные приложению дескрипторы хранилищ
     */
    public Collection<PackageSourceDescriptor> getAllPackageSourceDescriptors() {
        return getDescriptorMap().values();
    }

    /**
     * Закрытый конструктор
     */
    private DescriptorsFactory() {
    }

    /**
     * @return экземпляр фабрики
     */
    public static DescriptorsFactory getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new DescriptorsFactory();
                }
            }
        }
        return instance;
    }
    /**
     * Экземпляр фабрики
     */
    private static volatile DescriptorsFactory instance;
    /**
     * Монитор для безопасного ленивого создания фабрики
     */
    private static final Object monitor = new Object();
}

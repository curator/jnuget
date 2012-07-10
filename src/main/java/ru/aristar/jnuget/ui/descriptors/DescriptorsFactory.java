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
import ru.aristar.jnuget.sources.push.PushStrategy;

/**
 *
 * @author sviridov
 */
public class DescriptorsFactory {

    /**
     * Описания хранилищ по классам, которые они описывают
     */
    private final Map<Class<? extends PackageSource>, ObjectDescriptor<PackageSource>> sourceDescriptorsMap;
    /**
     * Oписания стратегий по классам, которые они описывают
     */
    private final Map<Class<? extends PushStrategy>, ObjectDescriptor<PushStrategy>> strategyDescriptorsMap;
    /**
     * Логгер
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Загружает все дескрипторы, указанные в файле по данному URL
     *
     * @param <S> класс для которого ищется дескриптор
     * @param url url файла с дескрипторами
     * @param s класс для которого ищется дескриптор
     * @return коллекция дескрипторов
     */
    private <S> Collection<ObjectDescriptor<S>> loadDescriptors(URL url, Class<S> s) {
        ArrayList<ObjectDescriptor<S>> result = new ArrayList<>();
        try {
            File file = new File(url.toURI());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                String className = bufferedReader.readLine();
                try {
                    Constructor<?> constructor = Class.forName(className).getConstructor();
                    Object o = constructor.newInstance();
                    if (o instanceof ObjectDescriptor) {
                        @SuppressWarnings("unchecked")
                        ObjectDescriptor<S> descriptor = (ObjectDescriptor<S>) o;
                        result.add(descriptor);
                    } else {
                        logger.error(format("Класс {0} не является ObjectDescriptor", className));
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
     * @param <S> тип, для котторого ищется дескриптор
     * @param s класс, для которого ищется дескриптор
     * @param descriptorsUrl url, по которому ищутся классы описаний
     * @return описания хранилищ по классам, которые они описывают
     */
    private <S> Map<Class<? extends S>, ObjectDescriptor<S>> loadDescriptors(Class<S> s, String descriptorsUrl) {
        Map<Class<? extends S>, ObjectDescriptor<S>> result = new HashMap<>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(descriptorsUrl);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Collection<ObjectDescriptor<S>> descriptors = loadDescriptors(url, s);
                for (ObjectDescriptor<S> descriptor : descriptors) {
                    result.put(descriptor.getObjectClass(), descriptor);
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
    public ObjectDescriptor<? extends PackageSource> getPackageSourceDescriptor(Class<? extends PackageSource> c) {
        return sourceDescriptorsMap.get(c);
    }

    /**
     * @return все доступные приложению дескрипторы хранилищ
     */
    public Collection<ObjectDescriptor<PackageSource>> getAllPackageSourceDescriptors() {
        return sourceDescriptorsMap.values();
    }

    /**
     * @param c класс стратегии фиксации
     * @return описание стратегии
     */
    public ObjectDescriptor<? extends PushStrategy> getPushStrategyDescriptor(Class<? extends PushStrategy> c) {
        return strategyDescriptorsMap.get(c);
    }

    /**
     * Закрытый конструктор
     */
    private DescriptorsFactory() {
        strategyDescriptorsMap = loadDescriptors(PushStrategy.class, "ru/aristar/jnuget/sources/strategyDescriptors.list");
        sourceDescriptorsMap = loadDescriptors(PackageSource.class, "ru/aristar/jnuget/sources/storageDescriptors.list");
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

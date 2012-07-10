package ru.aristar.jnuget.ui.descriptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    protected <S> Collection<ObjectDescriptor<S>> loadDescriptors(URL url, Class<S> s) {
        ArrayList<ObjectDescriptor<S>> result = new ArrayList<>();
        try {
            File file = new File(url.toURI());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                String className = bufferedReader.readLine();
                try {
                    Class<?> presentedClass = Class.forName(className);
                    if (ObjectDescriptor.class.isAssignableFrom(presentedClass)) {
                        @SuppressWarnings("unchecked")
                        Class<ObjectDescriptor<S>> descriptorClass = (Class<ObjectDescriptor<S>>) presentedClass;
                        Constructor<ObjectDescriptor<S>> constructor = descriptorClass.getConstructor();
                        ObjectDescriptor<S> descriptor = constructor.newInstance();
                        result.add(descriptor);
                    } else if (s.isAssignableFrom(presentedClass)) {
                        @SuppressWarnings("unchecked")
                        Class<S> targetClass = (Class<S>) presentedClass;
                        ObjectDescriptor<S> descriptor = createDesriptorForClass(targetClass);
                        result.add(descriptor);
                    } else {
                        logger.error(format("Класс {0} не является {1} или {2}", className, ObjectDescriptor.class, s));
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
     * Производит поиск пар GETTER/SETTER, анотированных как свойства в
     * указанном классе
     *
     * @param targetClass класс, в котором производится поиск свойств
     * @return коллекция свойств
     */
    private Collection<ObjectProperty> findAnnotatedProperties(Class<?> targetClass) {
        ArrayList<ObjectProperty> result = new ArrayList<>();
        for (Method method : targetClass.getMethods()) {
            try {
                Property property = method.getAnnotation(Property.class);
                if (property == null) {
                    continue;
                }
                final String propertyName = method.getName().substring(3);
                final String getterName = "get" + propertyName;
                final String setterName = "set" + propertyName;
                ObjectProperty objectProperty = new ObjectProperty(targetClass, property.description(), getterName, setterName);
                result.add(objectProperty);
            } catch (NoSuchMethodException e) {
                logger.error(format("Не удалось найти парный геттер/сеттер для класса {0}", targetClass), e);
            }
        }
        return result;
    }

    /**
     * Производит попытку создать дескриптор для класса
     *
     * @param <S> класс, для которого производится попытка создать дескриптор
     * @param targetClass класс, для которого производится попытка создать
     * дескриптор
     * @return дескриптор класса
     */
    protected <S> ObjectDescriptor<S> createDesriptorForClass(Class<? extends S> targetClass) {
        ArrayList<ObjectProperty> propertys = new ArrayList<>();
        propertys.addAll(findAnnotatedProperties(targetClass));
        AbstractObjectDescriptor<S> descriptor = new AbstractObjectDescriptor<>(targetClass, propertys);
        return descriptor;
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

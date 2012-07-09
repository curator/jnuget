package ru.aristar.jnuget.ui.descriptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static java.text.MessageFormat.format;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class DescriptorsFactory {

    private volatile Map<Class<? extends PackageSource>, PackageSourceDescriptor> descriptorsMap;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Class<? extends PackageSource>, PackageSourceDescriptor> getDescriptorMap() {
        if (descriptorsMap == null) {
            synchronized (this) {
                if (descriptorsMap == null) {
                    descriptorsMap = loadDescriptors();
                }
            }
        }
        return descriptorsMap;
    }

    private List<PackageSourceDescriptor> loadDescriptors(URL url) {
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
                        throw new NugetFormatException(format("Класс {0} не является PackageSourceDescriptor", className));
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NugetFormatException e) {
                    logger.error(format("Ошибка создания дескриптора типа {0}", className), e);
                }
            }
        } catch (URISyntaxException | IOException e) {
            logger.error(format("Ошибка чтения списка дескрипторов для URL {0}", url), e);
        }
        return result;
    }

    private Map<Class<? extends PackageSource>, PackageSourceDescriptor> loadDescriptors() {
        Map<Class<? extends PackageSource>, PackageSourceDescriptor> result = new HashMap<>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("ru/aristar/jnuget/sources/storageDescriptors.list");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                List<PackageSourceDescriptor> descriptors = loadDescriptors(url);
                for (PackageSourceDescriptor descriptor : descriptors) {
                    result.put(descriptor.getPackageSourceClass(), descriptor);
                }
            }
        } catch (IOException e) {
            logger.error("Не удалось загрузить описания источников пакетов", e);
        }
        return result;
    }

    public PackageSourceDescriptor getDescriptor(Class<? extends PackageSource> c) {
        return getDescriptorMap().get(c);
    }

    public Collection<PackageSourceDescriptor> getAllDescriptors() {
        return getDescriptorMap().values();
    }

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
    private static volatile DescriptorsFactory instance;
    private static Object monitor = new Object();
}

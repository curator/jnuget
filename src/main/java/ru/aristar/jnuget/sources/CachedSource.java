package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.ClassicNupkg;

/**
 * Класс, обеспечивающий промежуточное хранение кешированных результатов
 * запросов
 *
 * @author sviridov
 */
public class CachedSource implements PackageSource {

    /**
     * Исходное хранилище данных
     */
    private PackageSource source;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @return Исходное хранилище данных
     */
    public PackageSource getSource() {
        return source;
    }

    /**
     * @param source Исходное хранилище данных
     */
    public void setSource(PackageSource source) {
        this.source = source;
    }

    /**
     * @param source Исходное хранилище данных
     */
    public CachedSource(PackageSource source) {
        this.source = source;
    }

    /**
     * Конструктор по умолчанию
     */
    public CachedSource() {
    }

    /**
     * Очистка кеша
     */
    protected void clearCache() {
        try {
            JCS.getInstance("packages").clear();
        } catch (CacheException e) {
            logger.warn("Ошибка очистки кеша", e);
        }
    }

    /**
     * возвращаент сохраненный результат из кеша
     *
     * @param key ключ
     * @return сохраненный результат
     */
    private Object getValueFromCache(String key) {
        try {
            JCS cache = JCS.getInstance("packages");
            return (Collection<ClassicNupkg>) cache.get(key);
        } catch (CacheException ex) {
            logger.warn("Ошибка получения объекта из кеша", ex);
            return null;
        }
    }

    /**
     * Помещает значение в кеш
     *
     * @param key ключ
     * @param object объект для сохранения
     */
    private void putValueToCache(String key, Object object) {
        try {
            JCS cache = JCS.getInstance("packages");
            cache.put(key, object);
        } catch (CacheException ex) {
            logger.warn("Ошибка получения объекта из кеша", ex);
        }
    }

    @Override
    public Collection<ClassicNupkg> getPackages() {
        final String cacheName = "AllPackages";
        Collection<ClassicNupkg> result = (Collection<ClassicNupkg>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getPackages();
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public Collection<ClassicNupkg> getLastVersionPackages() {
        final String cacheName = "LastPackages";
        Collection<ClassicNupkg> result = (Collection<ClassicNupkg>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getLastVersionPackages();
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public Collection<ClassicNupkg> getPackages(String id) {
        final String cacheName = "package_" + id;
        Collection<ClassicNupkg> result = (Collection<ClassicNupkg>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getPackages(id);
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public Collection<ClassicNupkg> getPackages(String id, boolean ignoreCase) {
        if (!ignoreCase) {
            return getPackages(id);
        }
        final String cacheName = "package_igc_" + id;
        Collection<ClassicNupkg> result = (Collection<ClassicNupkg>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getPackages(id, true);
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public ClassicNupkg getLastVersionPackage(String id) {
        final String cacheName = "package_last_" + id;
        ClassicNupkg result = (ClassicNupkg) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getLastVersionPackage(id);
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public ClassicNupkg getLastVersionPackage(String id, boolean ignoreCase) {
        if (!ignoreCase) {
            return getLastVersionPackage(id);
        }
        final String cacheName = "package_last_igc_" + id;
        ClassicNupkg result = (ClassicNupkg) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getLastVersionPackage(id, true);
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public ClassicNupkg getPackage(String id, Version version) {
        return source.getPackage(id, version);
    }

    @Override
    public ClassicNupkg getPackage(String id, Version version, boolean ignoreCase) {
        return source.getPackage(id, version, ignoreCase);
    }

    @Override
    public boolean pushPackage(ClassicNupkg file, String apiKey) throws IOException {
        boolean result = source.pushPackage(file, apiKey);
        if (result) {
            clearCache();
        }
        return result;
    }

    @Override
    public PushStrategy getPushStrategy() {
        return source.getPushStrategy();
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        source.setPushStrategy(strategy);
    }
}

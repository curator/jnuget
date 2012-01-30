package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;

/**
 *
 * @author sviridov
 */
public class CachedSource implements PackageSource {

    private PackageSource source;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PackageSource getSource() {
        return source;
    }

    public void setSource(PackageSource source) {
        this.source = source;
    }

    public CachedSource(PackageSource source) {
        this.source = source;
    }

    public CachedSource() {
    }

    private Object getValueFromCache(String key) {
        try {
            JCS cache = JCS.getInstance("packages");
            return (Collection<NupkgFile>) cache.get(key);
        } catch (CacheException ex) {
            logger.warn("Ошибка получения объекта из кеша", ex);
            return null;
        }
    }

    private void putValueToCache(String key, Object object) {
        try {
            JCS cache = JCS.getInstance("packages");
            cache.put(key, object);
        } catch (CacheException ex) {
            logger.warn("Ошибка получения объекта из кеша", ex);
        }
    }

    @Override
    public Collection<NupkgFile> getPackages() {
        final String cacheName = "AllPackages";
        Collection<NupkgFile> result = (Collection<NupkgFile>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getPackages();
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public Collection<NupkgFile> getLastVersionPackages() {
        final String cacheName = "LastPackages";
        Collection<NupkgFile> result = (Collection<NupkgFile>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getLastVersionPackages();
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public Collection<NupkgFile> getPackages(String id) {
        final String cacheName = "package_" + id;
        Collection<NupkgFile> result = (Collection<NupkgFile>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getPackages(id);
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public Collection<NupkgFile> getPackages(String id, boolean ignoreCase) {
        if (!ignoreCase) {
            return getPackages(id);
        }
        final String cacheName = "package_igc_" + id;
        Collection<NupkgFile> result = (Collection<NupkgFile>) getValueFromCache(cacheName);
        if (result == null) {
            result = source.getPackages(id, true);
            putValueToCache(cacheName, result);
        }
        return result;
    }

    @Override
    public NupkgFile getLastVersionPackage(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getPackage(String id, Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getPackage(String id, Version version, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean pushPackage(NupkgFile file, String apiKey) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PushStrategy getPushStrategy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

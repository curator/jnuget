package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.ModifyStrategy;

/**
 *
 * @author sviridov
 */
public class PackageSourceGroup implements PackageSource<Nupkg> {

    /**
     * Источники пакетов
     */
    private volatile List<PackageSource<? extends Nupkg>> packageSources;
    /**
     * Стратегия публикации пакетов
     */
    private ModifyStrategy pushStrategy;
    /**
     * Имя хранилища.
     */
    private String name;
    /**
     * Имена источников пакетов
     */
    private ArrayList<String> sourceNames;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @return Источники пакетов
     */
    protected List<PackageSource<? extends Nupkg>> getSources() {
        if (packageSources == null) {
            synchronized (this) {
                if (packageSources == null) {
                    packageSources = loadPackageSources(sourceNames);
                }
            }
        }
        return packageSources;
    }

    /**
     * Восстанавливает хранилища по их именам
     *
     * @param packageSourcenames имена хранилищ
     * @return список хранилищ
     */
    private List<PackageSource<? extends Nupkg>> loadPackageSources(Collection<String> packageSourcenames) {
        ArrayList<PackageSource<? extends Nupkg>> result = new ArrayList<>();
        if (packageSourcenames != null) {
            for (String innerSourceName : packageSourcenames) {
                final PackageSource<Nupkg> packageSource = PackageSourceFactory.getInstance().getPackageSource(innerSourceName);
                if (packageSource != null && packageSource != this) {
                    result.add(packageSource);
                } else {
                    logger.warn("Некорректное вложенное хранилище: {}", new Object[]{packageSource});
                }
            }
        }
        return result;
    }

    @Override
    public Collection<Nupkg> getPackages() {
        ArrayList<Nupkg> result = new ArrayList<>();
        for (PackageSource<? extends Nupkg> source : getSources()) {
            result.addAll(source.getPackages());
        }
        return result;
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        HashMap<String, Nupkg> result = new HashMap<>();
        for (PackageSource<? extends Nupkg> source : getSources()) {
            for (Nupkg nupkgFile : source.getLastVersionPackages()) {
                String packageId = nupkgFile.getId();
                Nupkg storedPackage = result.get(packageId);
                if (storedPackage == null
                        || storedPackage.getVersion().compareTo(nupkgFile.getVersion()) < 0) {
                    result.put(packageId, nupkgFile);
                }
            }
        }
        return result.values();
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        HashMap<Version, Nupkg> result = new HashMap<>();
        for (PackageSource<? extends Nupkg> source : getSources()) {
            for (Nupkg file : source.getPackages(id)) {
                if (file != null) {
                    result.put(file.getVersion(), file);
                }
            }
        }
        return result.values();
    }

    @Override
    public Nupkg getLastVersionPackage(String id) {
        for (PackageSource source : getSources()) {
            Nupkg nupkgFile = source.getLastVersionPackage(id);
            if (nupkgFile != null) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public Nupkg getPackage(String id, Version version) {
        for (PackageSource source : getSources()) {
            Nupkg nupkgFile = source.getPackage(id, version);
            if (nupkgFile != null) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public boolean pushPackage(Nupkg nupkg) throws IOException {
        for (PackageSource source : getSources()) {
            if (source.pushPackage(nupkg)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ModifyStrategy getPushStrategy() {
        return pushStrategy;
    }

    @Override
    public void setPushStrategy(ModifyStrategy strategy) {
        this.pushStrategy = strategy;
    }

    @Override
    public void removePackage(Nupkg nupkg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refreshPackage(Nupkg nupkg) {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String storageName) {
        name = storageName;
    }

    /**
     * @return список имен вложеных хранилищ
     */
    public ArrayList<String> getInnerSourceNames() {
        ArrayList<String> result = new ArrayList<>();
        for (PackageSource packageSource : getSources()) {
            result.add(packageSource.getName());
        }
        return result;
    }

    /**
     * @param innerSourceNames список имен вложеных хранилищ
     */
    public synchronized void setInnerSourceNames(ArrayList<String> innerSourceNames) {
        packageSources = null;
        if (innerSourceNames != null) {
            sourceNames = new ArrayList<>(innerSourceNames.size());
            for (String innerSourceName : innerSourceNames) {
                if (innerSourceName == null || innerSourceName.equals("")) {
                    logger.warn("Задано пустое имя вложенного хранилища");
                    continue;
                }
                if (getName() != null && innerSourceName.equals(getName())) {
                    logger.warn("Рекурсивное назначение дочернего хранилища");
                    continue;
                }
                sourceNames.add(innerSourceName);
            }
        } else {
            sourceNames = new ArrayList<>();
        }
    }
}

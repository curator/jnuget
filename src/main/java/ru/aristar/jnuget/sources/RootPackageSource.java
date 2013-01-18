package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.ModifyStrategy;

/**
 *
 * @author sviridov
 */
public class RootPackageSource implements PackageSource<Nupkg> {

    /**
     * Источники пакетов
     */
    private LinkedList<PackageSource<Nupkg>> sources;
    /**
     * Стратегия публикации пакетов
     */
    private ModifyStrategy pushStrategy;

    /**
     * @return Источники пакетов
     */
    public LinkedList<PackageSource<Nupkg>> getSources() {
        if (sources == null) {
            sources = new LinkedList<>();
        }
        return sources;
    }

    /**
     * @param sources Источники пакетов
     */
    public void setSources(LinkedList<PackageSource<Nupkg>> sources) {
        this.sources = sources;
    }

    @Override
    public Collection<Nupkg> getPackages() {
        ArrayList<Nupkg> result = new ArrayList<>();
        for (PackageSource<Nupkg> source : getSources()) {
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
        return "root";
    }

    @Override
    public void setName(String storageName) {
    }
}

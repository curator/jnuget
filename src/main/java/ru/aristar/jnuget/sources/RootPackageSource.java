package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;

/**
 *
 * @author sviridov
 */
public class RootPackageSource implements PackageSource {

    /**
     * Источники пакетов
     */
    private LinkedList<PackageSource> sources;
    /**
     * Стратегия публикации пакетов
     */
    private PushStrategy pushStrategy;

    /**
     * @return Источники пакетов
     */
    public LinkedList<PackageSource> getSources() {
        if (sources == null) {
            sources = new LinkedList<>();
        }
        return sources;
    }

    /**
     * @param sources Источники пакетов
     */
    public void setSources(LinkedList<PackageSource> sources) {
        this.sources = sources;
    }

    @Override
    public Collection<NupkgFile> getPackages() {
        ArrayList<NupkgFile> result = new ArrayList<>();
        for (PackageSource source : getSources()) {
            result.addAll(source.getPackages());
        }
        return result;
    }

    @Override
    public Collection<NupkgFile> getLastVersionPackages() {
        HashMap<String, NupkgFile> result = new HashMap<>();
        for (PackageSource source : getSources()) {
            for (NupkgFile nupkgFile : source.getLastVersionPackages()) {
                String packageId = nupkgFile.getNuspecFile().getId();
                NupkgFile storedPackage = result.get(packageId);
                if (storedPackage == null
                        || storedPackage.getNuspecFile().getVersion().compareTo(nupkgFile.getNuspecFile().getVersion()) < 0) {
                    result.put(packageId, nupkgFile);
                }
            }
        }
        return result.values();
    }

    @Override
    public Collection<NupkgFile> getPackages(String id) {
        HashMap<Version, NupkgFile> result = new HashMap<>();
        for (PackageSource source : getSources()) {
            for (NupkgFile file : source.getPackages(id)) {
                result.put(file.getNuspecFile().getVersion(), file);
            }
        }
        return result.values();
    }

    @Override
    public Collection<NupkgFile> getPackages(String id, boolean ignoreCase) {
        HashMap<Version, NupkgFile> result = new HashMap<>();
        for (PackageSource source : getSources()) {
            for (NupkgFile file : source.getPackages(id, ignoreCase)) {
                result.put(file.getNuspecFile().getVersion(), file);
            }
        }
        return result.values();
    }

    @Override
    public NupkgFile getLastVersionPackage(String id) {
        for (PackageSource source : getSources()) {
            NupkgFile nupkgFile = source.getLastVersionPackage(id);
            if (nupkgFile != null) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public NupkgFile getLastVersionPackage(String id, boolean ignoreCase) {
        for (PackageSource source : getSources()) {
            NupkgFile nupkgFile = source.getLastVersionPackage(id, ignoreCase);
            if (nupkgFile != null) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public NupkgFile getPackage(String id, Version version) {
        for (PackageSource source : getSources()) {
            NupkgFile nupkgFile = source.getPackage(id, version);
            if (nupkgFile != null) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public NupkgFile getPackage(String id, Version version, boolean ignoreCase) {
        for (PackageSource source : getSources()) {
            NupkgFile nupkgFile = source.getPackage(id, version, ignoreCase);
            if (nupkgFile != null) {
                return nupkgFile;
            }
        }
        return null;
    }

    @Override
    public boolean pushPackage(NupkgFile file, String apiKey) throws IOException {
        for (PackageSource source : getSources()) {
            if (source.pushPackage(file, apiKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PushStrategy getPushStrategy() {
        return pushStrategy;
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        this.pushStrategy = strategy;
    }
}

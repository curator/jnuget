package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.MavenNupkg;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class ProxyPackageSource implements PackageSource<Nupkg> {

    /**
     * Локальное хранилище пакетов
     */
    protected MavenStylePackageSource hostedSource = new MavenStylePackageSource();
    /**
     * Удаленное хранилище пакетов
     */
    protected RemotePackageSource remoteSource = new RemotePackageSource();
    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Конструктор по умолчанию
     */
    public ProxyPackageSource() {
        hostedSource.setPushStrategy(new SimplePushStrategy(true));
    }

    /**
     * @return имя каталога, в котором находится хранилище пакетов
     */
    public String getRootFolderName() {
        return hostedSource.getRootFolderName();
    }

    /**
     * @param folderName имя каталога, в котором находится хранилище пакетов
     */
    public void setRootFolderName(String folderName) {
        hostedSource.setRootFolderName(folderName);
    }

    @Override
    public Collection<Nupkg> getPackages() {
        ArrayList<Nupkg> nupkgs = new ArrayList<>();
        try {
            nupkgs.addAll(remoteSource.getPackages());
        } catch (Exception e) {
            logger.warn("Не удалось получить пакеты из удаленного хранилища", e);
        }
        nupkgs.addAll(hostedSource.getPackages());
        return nupkgs;
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        return getPackages(id, true);
    }

    @Override
    public Collection<Nupkg> getPackages(String id, boolean ignoreCase) {
        HashMap<Version, Nupkg> packages = new HashMap<>();
        try {
            for (Nupkg nupkg : remoteSource.getPackages(id)) {
                packages.put(nupkg.getVersion(), nupkg);
            }
        } catch (Exception e) {
            logger.warn("Не удалось получить пакеты из удаленного хранилища", e);
        }
        for (Nupkg nupkg : hostedSource.getPackages(id)) {
            packages.put(nupkg.getVersion(), nupkg);
        }
        return packages.values();
    }

    @Override
    public Nupkg getLastVersionPackage(String id) {
        return getLastVersionPackage(id, true);
    }

    @Override
    public Nupkg getLastVersionPackage(String id, boolean ignoreCase) {
        Collection<Nupkg> nupkgs = getPackages(id);
        if (nupkgs == null || nupkgs.isEmpty()) {
            return null;
        }
        Nupkg result = null;
        for (Nupkg nupkg : nupkgs) {
            if (result == null || result.getVersion().compareTo(nupkg.getVersion()) < 0) {
                result = nupkg;
            }
        }
        return result;
    }

    @Override
    public MavenNupkg getPackage(String id, Version version) {
        return getPackage(id, version, true);
    }

    @Override
    public MavenNupkg getPackage(String id, Version version, boolean ignoreCase) {
        MavenNupkg nupkg = hostedSource.getPackage(id, version);
        if (nupkg == null) {
            try {
                Nupkg remoteNupkg = remoteSource.getPackage(id, version);
                hostedSource.pushPackage(remoteNupkg, null);
                nupkg = hostedSource.getPackage(id, version);
            } catch (Exception e) {
                logger.warn("Ошибка помещения файла в локальное хранилище", e);
            }
        }
        return nupkg;
    }

    @Override
    public boolean pushPackage(Nupkg file, String apiKey) throws IOException {
        return false;
    }

    @Override
    public PushStrategy getPushStrategy() {
        return hostedSource.getPushStrategy();
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        hostedSource.setPushStrategy(strategy);
    }
}

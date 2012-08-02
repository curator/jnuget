package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.MavenNupkg;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.ProxyNupkg;
import ru.aristar.jnuget.files.RemoteNupkg;
import ru.aristar.jnuget.sources.push.PushStrategy;
import ru.aristar.jnuget.sources.push.PushStrategy;
import ru.aristar.jnuget.ui.descriptors.Property;

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
        hostedSource.setPushStrategy(new PushStrategy(true));
    }

    /**
     * @return имя каталога, в котором находится хранилище пакетов
     */
    @Property
    public String getFolderName() {
        return hostedSource.getRootFolderName();
    }

    /**
     * @param folderName имя каталога, в котором находится хранилище пакетов
     */
    public void setFolderName(String folderName) {
        hostedSource.setRootFolderName(folderName);
    }

    /**
     * @param url URL удаленного хранилища
     */
    @Property
    public void setUrl(String url) {
        remoteSource.setUrl(url);
    }

    /**
     * @return URL удаленного хранилища
     */
    public String getUrl() {
        return remoteSource.getUrl();
    }

    @Override
    public Collection<Nupkg> getPackages() {
        ArrayList<Nupkg> nupkgs = new ArrayList<>();
        try {
            for (RemoteNupkg remoteNupkg : remoteSource.getPackages()) {
                nupkgs.add(new ProxyNupkg(hostedSource, remoteNupkg));
            }
        } catch (Exception e) {
            logger.warn("Не удалось получить пакеты из удаленного хранилища", e);
        }
        nupkgs.addAll(hostedSource.getPackages());
        return nupkgs;
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        Collection<Nupkg> nupkgs = new HashSet<>();
        try {
            for (RemoteNupkg remoteNupkg : remoteSource.getLastVersionPackages()) {
                nupkgs.add(new ProxyNupkg(hostedSource, remoteNupkg));
            }
        } catch (Exception e) {
            logger.warn("Не удалось получить пакеты из удаленного хранилища", e);
        }
        nupkgs.addAll(hostedSource.getLastVersionPackages());
        nupkgs = ClassicPackageSource.extractLastVersion(nupkgs);
        return nupkgs;
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        HashMap<Version, Nupkg> packages = new HashMap<>();
        try {
            for (RemoteNupkg remoteNupkg : remoteSource.getPackages(id)) {
                packages.put(remoteNupkg.getVersion(), new ProxyNupkg(hostedSource, remoteNupkg));
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
        MavenNupkg nupkg = hostedSource.getPackage(id, version);
        if (nupkg == null) {
            try {
                logger.debug("Получение файла пакета {}:{} из удаленного хранилища", new Object[]{id, version});
                RemoteNupkg remoteNupkg = remoteSource.getPackage(id, version);
                if (remoteNupkg == null) {
                    return null;
                }
                boolean result = hostedSource.pushPackage(remoteNupkg);
                if (result) {
                    nupkg = hostedSource.getPackage(id, version);
                } else {
                    logger.warn("Не удалось поместить пакет {}:{} в локальное хранилище",
                            new Object[]{remoteNupkg.getId(), remoteNupkg.getVersion()});
                }
            } catch (Exception e) {
                logger.warn("Ошибка помещения файла в локальное хранилище", e);
            }
        }
        return nupkg;
    }

    @Override
    public boolean pushPackage(Nupkg file) throws IOException {
        return false;
    }

    @Override
    public PushStrategy getPushStrategy() {
        return hostedSource.getPushStrategy();
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        remoteSource.setPushStrategy(strategy);
    }

    @Override
    public void removePackage(Nupkg nupkg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        final String url = remoteSource == null ? null : remoteSource.getUrl();
        final String folder = hostedSource == null ? null : hostedSource.getRootFolderName();
        return "ProxyPackageSource{" + url + " --> " + folder + '}';
    }

    @Override
    public void refreshPackage(Nupkg nupkg) {
        if (nupkg instanceof ProxyNupkg) {
            ProxyNupkg proxyNupkg = (ProxyNupkg) nupkg;
            proxyNupkg.setPackageSource(hostedSource);
        }
    }
}

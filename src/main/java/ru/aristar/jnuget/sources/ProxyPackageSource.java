package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.MavenNupkg;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class ProxyPackageSource implements PackageSource<MavenNupkg> {

    /**
     * Локальное хранилище пакетов
     */
    private MavenStylePackageSource hostedSource = new MavenStylePackageSource();
    /**
     * Удаленное хранилище пакетов
     */
    private RemotePackageSource remoteSource = new RemotePackageSource();

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
    public Collection<MavenNupkg> getPackages() {
        try {
            Collection<Nupkg> nupkgs = remoteSource.getPackages();
        } catch (Exception e) {
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<MavenNupkg> getLastVersionPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<MavenNupkg> getPackages(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<MavenNupkg> getPackages(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MavenNupkg getLastVersionPackage(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MavenNupkg getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MavenNupkg getPackage(String id, Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MavenNupkg getPackage(String id, Version version, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean pushPackage(Nupkg file, String apiKey) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
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

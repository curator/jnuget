package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class RemotePackageSource implements PackageSource<Nupkg> {

    /**
     * Удаленное хранилище пакетов
     */
    private NugetClient remoteStorage;

    @Override
    public Nupkg getLastVersionPackage(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getPackage(String id, Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getPackage(String id, Version version, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getPackages(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PushStrategy getPushStrategy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean pushPackage(Nupkg file, String apiKey) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

package ru.aristar.jnuget.sources;

import java.io.File;

/**
 * Генератор информации о пакетах для хранилища Nuget.
 *
 * @author Unlocker
 */
public class NugetPackageInfoProvider implements PackageInfoProvider {
    
    /**
     * Корневая папка хранилища.
     */
    private final File rootFolder;

    /**
     * Генератор информации о пакетах для хранилища Nuget.
     *
     * @param rootFolder Корневая папка хранилища.
     */
    public NugetPackageInfoProvider(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public PackageInfo parseInfo(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

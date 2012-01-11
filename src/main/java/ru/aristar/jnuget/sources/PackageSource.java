package ru.aristar.jnuget.sources;

import java.util.Collection;
import ru.aristar.jnuget.files.NupkgFile;

/**
 *
 * @author sviridov
 */
public interface PackageSource {

    /**
     * Возвращает полный список пакетов в хранилище
     *
     * @return коллекция файлов пакетов
     */
    Collection<NupkgFile> getPackages();
}

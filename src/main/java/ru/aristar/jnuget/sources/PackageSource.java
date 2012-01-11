package ru.aristar.jnuget.sources;

import java.util.Collection;
import ru.aristar.jnuget.Version;
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

    /**
     * Возвращает пакет с указанной версией и идентификатором
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return
     */
    NupkgFile getPackage(String id, Version version);
}

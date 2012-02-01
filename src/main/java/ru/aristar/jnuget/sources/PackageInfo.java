package ru.aristar.jnuget.sources;

import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Hash;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;

/**
 * Краткая информация о пакете.
 *
 * @author Unlocker
 */
public interface PackageInfo {

    /**
     * Идентификатор пакета.
     *
     * @return Идентификатор пакета.
     */
    String getId();

    /**
     * Версия пакета.
     *
     * @return Версия пакета.
     */
    Version getVersion();

    /**
     * Тело пакета.
     *
     * @return Тело пакета.
     */
    NupkgFile getPackage();

    /**
     * Спецификация.
     *
     * @return Спецификация.
     */
    NuspecFile getSpec();

    /**
     * Контрольная сумма.
     *
     * @return Контрольная сумма.
     */
    Hash getHash();
}

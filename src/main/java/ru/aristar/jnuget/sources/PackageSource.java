package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;

/**
 * Контракт источника пакетов
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
     * Возвращает список пакетов, содержащий только последние версии
     *
     * @return коллекция файлов пакетов
     */
    Collection<NupkgFile> getLastVersionPackages();

    /**
     * Возвращает пакеты с указанным идентификатором
     *
     * @param id
     * @return коллекция файлов пакетов
     */
    Collection<NupkgFile> getPackages(String id);

    /**
     * Возвращает список пакетов с указанным идентификатором
     *
     * @param id идентификатор пакета
     * @param ignoreCase нужно ли игнорировать регистр символов
     * @return коллекция файлов пакетов
     */
    Collection<NupkgFile> getPackages(String id, boolean ignoreCase);

    /**
     * Возвращает последнюю версию указанного пакета
     *
     * @param id идентификатор пакета
     * @return файл пакета
     */
    NupkgFile getLastVersionPackage(String id);

    /**
     * Возвращает последнюю версию указанного пакета
     *
     * @param id идентификатор пакета
     * @param ignoreCase нужно ли игнорировать регистр символов
     * @return файл пакета
     */
    NupkgFile getLastVersionPackage(String id, boolean ignoreCase);

    /**
     * Возвращает пакет с указанной версией и идентификатором
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return файл пакета
     */
    NupkgFile getPackage(String id, Version version);

    /**
     * Возвращает пакет с указанной версией и идентификатором
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @param ignoreCase нужно ли игнорировать регистр символов
     * @return файл пакета
     */
    NupkgFile getPackage(String id, Version version, boolean ignoreCase);

    /**
     * Загружает пакет в хранилище
     *
     * @param file Файл для загрузки
     */
    void pushPackage(NupkgFile file) throws IOException;
}

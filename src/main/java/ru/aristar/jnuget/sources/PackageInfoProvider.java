package ru.aristar.jnuget.sources;

import java.io.File;

/**
 * Генератор информации о пакете.
 *
 * @author Unlocker
 */
public interface PackageInfoProvider {

    /**
     * Получает объект с информацией о пакете.
     *
     * @param file Адрес пакета.
     * @return Информация о пакете.
     */
    PackageInfo parseInfo(File file);
}

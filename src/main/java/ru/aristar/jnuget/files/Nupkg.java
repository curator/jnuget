package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.EnumSet;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.nuspec.NuspecFile;

/**
 * Интерфейс пакета NuGet
 *
 * @author Unlocker
 */
public interface Nupkg extends Serializable {

    /**
     * Расширение по умолчанию
     */
    String DEFAULT_EXTENSION = ".nupkg";

    /**
     * @return имя файла пакета
     */
    String getFileName();

    /**
     * @return HASH файла пакета
     * @throws NoSuchAlgorithmException в системе не найден алгоритм расчета
     * HASH
     * @throws IOException ошибка чтения данных
     */
    Hash getHash() throws NoSuchAlgorithmException, IOException;

    /**
     * @return файл спецификации пакета
     * @throws NugetFormatException ошмбка чтения спецификации пакета
     */
    NuspecFile getNuspecFile() throws NugetFormatException;

    /**
     * @return размер пакета
     */
    Long getSize();

    /**
     * @return поток с данными пакета
     * @throws IOException ошибка чтения данных
     */
    InputStream getStream() throws IOException;

    /**
     * @return дата обновления пакета
     */
    Date getUpdated();

    /**
     * @return идентификатор пакета
     */
    String getId();

    /**
     * @return версия пакета
     */
    Version getVersion();

    /**
     * @return набор фрейморков для которых есть реализация
     */
    EnumSet<Framework> getTargetFramework();

    /**
     * Загружает все ленивые поля пакета
     *
     * @throws IOException ошибка получения данных
     */
    void load() throws IOException;
}

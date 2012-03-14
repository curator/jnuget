package ru.aristar.jnuget.files;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import ru.aristar.jnuget.Version;

/**
 * Интерфейс пакета NuGet
 *
 * @author Unlocker
 */
public interface Nupkg {

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
     */
    NuspecFile getNuspecFile();

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
     * Загружает все ленивые поля пакета
     *
     * @throws IOException ошибка получения данных
     */
    void load() throws IOException;
}

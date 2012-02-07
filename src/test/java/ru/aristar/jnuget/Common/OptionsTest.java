package ru.aristar.jnuget.Common;

import java.io.File;
import java.io.InputStream;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class OptionsTest {

    /**
     * проверка загрузки настроек по умолчанию
     */
    @Test
    public void testGetDefaultOptions() {
        //GIVEN
        //TODO Переделать тест так, чтобы он не заменял файл в папке пользователя
        final String userHome = System.getProperty("user.home");
        File file = new File(userHome + "/.nuget/" + Options.DEFAULT_OPTIONS_FILE_NAME);
        file.delete();
        assertFalse("Файла с настройками не должно существовать перед тестом", file.exists());
        //WHEN
        Options options = Options.loadOptions();
        //THEN
        assertTrue("Файл с настройками должен быть создан", file.exists());
        assertEquals("Файл с настройками содержит одно хранилище", 1, options.getStorageOptionsList().size());
        assertEquals("Имя папки с пакетами", "${nuget.home}/Packages/", options.getStorageOptionsList().get(0).getProperties().get("folderName"));
        assertNull("По умолчанию стратегия пула не задается", options.getStorageOptionsList().get(0).getStrategyOptions());
        //TEARDOWN
        file.delete();
    }

    /**
     * Проверка чтения настроек из XML файла
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseOptions() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/Options/jnuget.test.config.xml");
        //WHEN
        Options options = Options.parse(inputStream);
        //THEN        
        assertEquals("Количество хранилищ", 2, options.getStorageOptionsList().size());
        assertEquals("Класс хранилища 1", "TEST_CLASS_1", options.getStorageOptionsList().get(0).getClassName());
        assertEquals("Имя папки хранилища 1", "TEST_FOLDER_1", options.getStorageOptionsList().get(0).getProperties().get("folderName"));
        assertEquals("Ключ доступа хранилища 1", "value_1", options.getStorageOptionsList().get(0).getStrategyOptions().getProperties().get("property_1"));
        assertEquals("Класс стратегии хранилища 1", "PUSH_CLASS_1", options.getStorageOptionsList().get(0).getStrategyOptions().getClassName());
        assertEquals("Класс хранилища 2", "TEST_CLASS_2", options.getStorageOptionsList().get(1).getClassName());
        assertEquals("Имя папки хранилища 2", "TEST_FOLDER_2", options.getStorageOptionsList().get(1).getProperties().get("folderName"));
        assertNull("Стратегия хранилища 2", options.getStorageOptionsList().get(1).getStrategyOptions());
    }
}

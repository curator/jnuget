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
        final String userHome = System.getProperty("user.home");
        File file = new File(userHome + "/.nuget/" + Options.DEFAULT_OPTIONS_FILE_NAME);
        file.delete();
        assertFalse("Файла с настройками не должно существовать перед тестом", file.exists());
        //WHEN
        Options options = Options.loadOptions();
        //THEN
        assertTrue("Файл с настройками должен быть создан", file.exists());
        assertEquals("Имя папки с пакетами", "${nuget.home}/Packages/", options.getFolderName());
        assertNull("По умолчанию стратегия пула не задается", options.getApiKey());
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
        assertEquals("Каталог с пакетами", "TEST_FOLDER", options.getFolderName());
        assertEquals("Ключ доступа", "TEST_API_KEY", options.getApiKey());
    }
}

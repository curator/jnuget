package ru.aristar.jnuget.Common;

import java.io.File;
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
        File file = new File(Options.DEFAULT_OPTIONS_FILE_NAME);
        file.delete();
        assertFalse("Файла с настройками не должно существовать перед тестом", file.exists());
        //WHEN
        Options options = Options.loadOptions();
        //THEN
        assertTrue("Файл с настройками должен быть создан", file.exists());
        assertEquals("Имя папки с пакетами", "c:/inetpub/wwwroot/nuget/Packages/", options.getFolderName());
        //TEARDOWN
        file.delete();
    }
}

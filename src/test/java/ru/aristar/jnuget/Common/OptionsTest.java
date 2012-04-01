package ru.aristar.jnuget.Common;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Тест настроек сервера
 *
 * @author sviridov
 */
public class OptionsTest {

    /**
     * проверка загрузки настроек по умолчанию
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetDefaultOptions() throws Exception {
        //GIVEN
        File tempFolder = File.createTempFile("tmp", "tmp").getParentFile();
        String oldHome = System.getProperty("user.home");
        System.setProperty("user.home", tempFolder.getAbsolutePath());
        System.setProperty("nuget.home", "");
        File nugetHome = new File(tempFolder, ".nuget");
        File file = new File(nugetHome, Options.DEFAULT_OPTIONS_FILE_NAME);
        file.delete();
        assertFalse("Файла с настройками не должно существовать перед тестом", file.exists());
        //WHEN
        Options options = Options.loadOptions();
        //THEN 
        assertTrue("Файл с настройками должен быть создан", file.exists());
        assertNull("Стратегия корневого хранилища не устанавливается", options.getStrategyOptions());
        assertEquals("Файл с настройками содержит одно хранилище", 1, options.getStorageOptionsList().size());
        assertEquals("Имя папки с пакетами", "${nuget.home}/Packages/", options.getStorageOptionsList().get(0).getProperties().get("folderName"));
        assertNull("По умолчанию стратегия пула не задается", options.getStorageOptionsList().get(0).getStrategyOptions());
        //TEARDOWN
        file.delete();
        nugetHome.delete();
        System.setProperty("user.home", oldHome);
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
        assertEquals("Класс корневой стратегии фиксации", "PUSH_CLASS_GLOBAL", options.getStrategyOptions().getClassName());
        assertEquals("Свойство стратегии фиксации", "value_g", options.getStrategyOptions().getProperties().get("property_g"));
        assertEquals("Количество хранилищ", 2, options.getStorageOptionsList().size());
        assertEquals("Класс хранилища 1", "TEST_CLASS_1", options.getStorageOptionsList().get(0).getClassName());
        assertFalse("Xранилище 1 индексируется", options.getStorageOptionsList().get(0).isIndexed());
        assertEquals("Имя папки хранилища 1", "TEST_FOLDER_1", options.getStorageOptionsList().get(0).getProperties().get("folderName"));
        assertEquals("Ключ доступа хранилища 1", "value_1", options.getStorageOptionsList().get(0).getStrategyOptions().getProperties().get("property_1"));
        assertEquals("Класс стратегии хранилища 1", "PUSH_CLASS_1", options.getStorageOptionsList().get(0).getStrategyOptions().getClassName());
        assertEquals("Класс хранилища 2", "TEST_CLASS_2", options.getStorageOptionsList().get(1).getClassName());
        assertTrue("Xранилище 2 индексируется", options.getStorageOptionsList().get(1).isIndexed());
        assertEquals("Имя папки хранилища 2", "TEST_FOLDER_2", options.getStorageOptionsList().get(1).getProperties().get("folderName"));
        assertNull("Стратегия хранилища 2", options.getStorageOptionsList().get(1).getStrategyOptions());
    }

    /**
     * Проверка чтения настроек триггеров из файла.
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseTriggerOptions() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/Options/jnuget.push.trigger.config.xml");
        //WHEN 
        Options options = Options.parse(inputStream);
        //THEN
        List<StorageOptions> storageOptions = options.getStorageOptionsList();
        assertThat("Создан набор настроек для хранилища", storageOptions.size(), equalTo(1));
        PushStrategyOptions pushStrategyOptions = storageOptions.get(0).getStrategyOptions();
        assertThat("Создана стратегия фиксации пакета", pushStrategyOptions, is(notNullValue()));
        assertThat("Созданы настройки триггера, выполняющегося до вставки",
                pushStrategyOptions.getAftherTriggersOptions().size(), equalTo(1));
        TriggerOptions beforeTriggerOptions = pushStrategyOptions.getBeforeTriggersOptions().get(0);
        assertThat("Имя класса триггера", beforeTriggerOptions.getClassName(), equalTo("TRIGGER_CLASS_1"));
        assertThat("Названия свойств триггера",
                beforeTriggerOptions.getProperties().keySet().toArray(new String[]{}),
                equalTo(new String[]{"property_2"}));
        assertThat("Значения свойств триггера",
                beforeTriggerOptions.getProperties().keySet().toArray(new String[]{}),
                equalTo(new String[]{"value_2"}));
        assertThat("Созданы настройки триггера, выполняющегося после вставки",
                pushStrategyOptions.getAftherTriggersOptions().size(), equalTo(1));
        TriggerOptions aftherTriggerOptions = pushStrategyOptions.getAftherTriggersOptions().get(0);
        assertThat("Имя класса триггера", aftherTriggerOptions.getClassName(), equalTo("TRIGGER_CLASS_2"));
        assertThat("Названия свойств триггера",
                beforeTriggerOptions.getProperties().keySet().toArray(new String[]{}),
                equalTo(new String[]{"property_3"}));
        assertThat("Значения свойств триггера",
                beforeTriggerOptions.getProperties().keySet().toArray(new String[]{}),
                equalTo(new String[]{"value_3"}));
    }
}

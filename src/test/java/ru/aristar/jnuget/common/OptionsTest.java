package ru.aristar.jnuget.common;

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
        File nugetHome = new File(tempFolder, ".nuget");
        System.setProperty("nuget.home", nugetHome.getAbsolutePath());
        File file = new File(nugetHome, Options.DEFAULT_OPTIONS_FILE_NAME);
        file.delete();
        assertFalse("Файла с настройками не должно существовать перед тестом", file.exists());
        //WHEN
        Options options = Options.loadOptions();
        //THEN
        assertTrue("Файл с настройками должен быть создан", file.exists());
        assertEquals("Файл с настройками содержит одно хранилище", 1, options.getStorageOptionsList().size());
        assertEquals("Имя папки с пакетами", "${nuget.home}/Packages/", options.getStorageOptionsList().get(0).getProperties().get("folderName").toArray()[0]);
        assertFalse("По умолчанию удаление пакетов запрещено", options.getStorageOptionsList().get(0).isCanDelete());
        assertFalse("По умолчанию публикация пакетов запрещена", options.getStorageOptionsList().get(0).isCanPush());
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
        InputStream inputStream = this.getClass().getResourceAsStream("/options/jnuget.test.config.xml");
        //WHEN
        Options options = Options.parse(inputStream);
        //THEN
        assertEquals("Количество хранилищ", 2, options.getStorageOptionsList().size());
        assertEquals("Класс хранилища 1", "TEST_CLASS_1", options.getStorageOptionsList().get(0).getClassName());
        assertFalse("Xранилище 1 индексируется", options.getStorageOptionsList().get(0).isIndexed());
        assertEquals("Имя папки хранилища 1", "TEST_FOLDER_1", options.getStorageOptionsList().get(0).getProperties().get("folderName").toArray()[0]);
        assertThat(options.getStorageOptionsList().get(0).isCanPush(), is(equalTo(true)));
        assertThat(options.getStorageOptionsList().get(0).isCanDelete(), is(equalTo(false)));
        assertEquals("Класс хранилища 2", "TEST_CLASS_2", options.getStorageOptionsList().get(1).getClassName());
        assertTrue("Xранилище 2 индексируется", options.getStorageOptionsList().get(1).isIndexed());
        assertEquals("Имя папки хранилища 2", "TEST_FOLDER_2", options.getStorageOptionsList().get(1).getProperties().get("folderName").toArray()[0]);
        assertThat(options.getStorageOptionsList().get(1).isCanPush(), is(equalTo(false)));
        assertThat(options.getStorageOptionsList().get(1).isCanDelete(), is(equalTo(true)));
    }

    /**
     * Проверка чтения настроек триггеров из файла.
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseTriggerOptions() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/options/jnuget.push.trigger.config.xml");
        //WHEN
        Options options = Options.parse(inputStream);
        //THEN
        List<StorageOptions> storageOptions = options.getStorageOptionsList();
        assertThat("Создан набор настроек для хранилища", storageOptions.size(), equalTo(1));
        assertThat(storageOptions.get(0).isCanPush(), is(equalTo(true)));
        assertThat(storageOptions.get(0).isCanDelete(), is(equalTo(true)));
        assertThat("Количество настроек триггеров, выполняющихся до вставки",
                storageOptions.get(0).getAftherTriggersOptions().size(), equalTo(1));
        TriggerOptions beforeTriggerOptions = storageOptions.get(0).getBeforeTriggersOptions().get(0);
        assertThat("Имя класса триггера", beforeTriggerOptions.getClassName(), equalTo("TRIGGER_CLASS_1"));
        assertThat("Названия свойств триггера",
                beforeTriggerOptions.getProperties().keySet().toArray(new String[]{}),
                equalTo(new String[]{"property_2"}));
        assertThat("Значения свойств триггера",
                beforeTriggerOptions.getProperties().values().toArray(new String[]{}),
                equalTo(new String[]{"value_2"}));
        assertThat("Количество настроек триггеров, выполняющихся после вставки",
                storageOptions.get(0).getAftherTriggersOptions().size(), equalTo(1));
        TriggerOptions aftherTriggerOptions = storageOptions.get(0).getAftherTriggersOptions().get(0);
        assertThat("Имя класса триггера", aftherTriggerOptions.getClassName(), equalTo("TRIGGER_CLASS_2"));
        assertThat("Названия свойств триггера",
                aftherTriggerOptions.getProperties().keySet().toArray(new String[]{}),
                equalTo(new String[]{"property_3"}));
        assertThat("Значения свойств триггера",
                aftherTriggerOptions.getProperties().values().toArray(new String[]{}),
                equalTo(new String[]{"value_3"}));
    }
}

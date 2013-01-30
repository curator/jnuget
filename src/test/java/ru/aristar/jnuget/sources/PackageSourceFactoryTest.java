package ru.aristar.jnuget.sources;

import java.io.File;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.common.StorageOptions;
import ru.aristar.jnuget.common.TriggerOptions;
import ru.aristar.jnuget.sources.push.*;

/**
 *
 * @author sviridov
 */
public class PackageSourceFactoryTest {

    /**
     * Проверка создания файлового хранилища на основе настроек
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateFilePackageSource() throws Exception {
        //GIVEN
        final String userHomeFolder = System.getProperty("user.home");
        PackageSourceFactory sourceFactory = new PackageSourceFactory();
        StorageOptions storageOptions = new StorageOptions();
        storageOptions.setClassName(ClassicPackageSource.class.getCanonicalName());
        storageOptions.setIndexed(false);
        storageOptions.getProperties().put("folderName", "${user.home}/Packages/");
        //WHEN
        PackageSource result = sourceFactory.createPackageSource(storageOptions);
        final File expectedFolder = new File(userHomeFolder + "/Packages/");
        //THEN        
        assertEquals("Класс хранилища", ClassicPackageSource.class, result.getClass());
        assertEquals("Корневой каталог хранилища", expectedFolder.getAbsolutePath(), ((ClassicPackageSource) result).getFolderName());
    }

    /**
     * Проверка создания триггеров стратегии фиксации
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreatePushStrategyTriggers() throws Exception {
        //GIVEN
        PackageSourceFactory sourceFactory = new PackageSourceFactory();
        final TriggerOptions triggerOptions = new TriggerOptions();
        triggerOptions.setClassName(RemoveOldVersionTrigger.class.getCanonicalName());
        triggerOptions.getProperties().put("maxPackageCount", "5");
        //WHEN
        AfterTrigger pushTrigger = sourceFactory.createTrigger(triggerOptions, AfterTrigger.class);
        //THEN
        assertThat("Триггер создан", pushTrigger, is(notNullValue()));
        assertThat("Созданый триггер", pushTrigger, instanceOf(RemoveOldVersionTrigger.class));
        RemoveOldVersionTrigger removeOldVersionTrigger = (RemoveOldVersionTrigger) pushTrigger;
        assertThat("Количество пакетов, разрешенных для сохранения",
                removeOldVersionTrigger.getMaxPackageCount(),
                equalTo(5));
    }

    /**
     * Проверка создания стратегии публикации на основе настроек, содержащих
     * триггеры
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreatePushStrategyWithTriggers() throws Exception {
        //GIVEN
        PackageSourceFactory sourceFactory = new PackageSourceFactory();
        //Триггер после помещения пакета
        final TriggerOptions aftherTriggerOptions = new TriggerOptions();
        aftherTriggerOptions.setClassName(RemoveOldVersionTrigger.class.getCanonicalName());
        aftherTriggerOptions.getProperties().put("maxPackageCount", "5");
        //Триггер до помещения пакета
        final TriggerOptions beforeTriggerOptions = new TriggerOptions();
        beforeTriggerOptions.setClassName(TestBeforeTrigger.class.getCanonicalName());
        beforeTriggerOptions.getProperties().put("testProperty", "15");
        //Стратегия фиксации
        StorageOptions storageOptions = new StorageOptions();
        storageOptions.getAftherTriggersOptions().add(aftherTriggerOptions);
        storageOptions.getBeforeTriggersOptions().add(beforeTriggerOptions);
        //WHEN
        ModifyStrategy result = sourceFactory.createPushStrategy(storageOptions);
        //THEN
        assertThat("Стратегия фиксации", result, instanceOf(ModifyStrategy.class));
        assertThat("Количество созданых before тригеров", result.getBeforePushTriggers().size(), equalTo(1));
        assertThat("Количество созданых afther тригеров", result.getAftherPushTriggers().size(), equalTo(1));
        assertThat("Триггер before", result.getBeforePushTriggers().get(0), instanceOf(TestBeforeTrigger.class));
        TestBeforeTrigger beforeTrigger = (TestBeforeTrigger) result.getBeforePushTriggers().get(0);
        assertThat("Тестовое свойство триггера before ", beforeTrigger.getTestProperty(), equalTo(15));
        assertThat("Триггер afther", result.getAftherPushTriggers().get(0), instanceOf(RemoveOldVersionTrigger.class));
        RemoveOldVersionTrigger aftherTrigger = (RemoveOldVersionTrigger) result.getAftherPushTriggers().get(0);
        assertThat("Количество пакетов триггера afther ", aftherTrigger.getMaxPackageCount(), equalTo(5));
    }
}

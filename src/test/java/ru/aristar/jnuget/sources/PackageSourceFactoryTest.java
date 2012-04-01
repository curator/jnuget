package ru.aristar.jnuget.sources;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import ru.aristar.jnuget.Common.Options;
import ru.aristar.jnuget.Common.PushStrategyOptions;
import ru.aristar.jnuget.Common.StorageOptions;
import ru.aristar.jnuget.Common.TriggerOptions;
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
        storageOptions.setClassName(FilePackageSource.class.getCanonicalName());
        storageOptions.setIndexed(false);
        storageOptions.getProperties().put("folderName", "${user.home}/Packages/");
        //WHEN
        PackageSource result = sourceFactory.createPackageSource(storageOptions);
        final File expectedFolder = new File(userHomeFolder + "/Packages/");
        //THEN        
        assertEquals("Класс хранилища", FilePackageSource.class, result.getClass());
        assertEquals("Корневой каталог хранилища", expectedFolder.getAbsolutePath(), ((FilePackageSource) result).getFolderName());
    }

    /**
     * Проверка создания стратегии публикации на основе настроек
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateApiKeyPushStrategy() throws Exception {
        //GIVEN
        PackageSourceFactory sourceFactory = new PackageSourceFactory();
        final PushStrategyOptions pushStrategyOptions = new PushStrategyOptions();
        pushStrategyOptions.setClassName(ApiKeyPushStrategy.class.getCanonicalName());
        pushStrategyOptions.getProperties().put("apiKey", "TEST_API_KEY");
        //WHEN
        PushStrategy result = sourceFactory.createPushStrategy(pushStrategyOptions);
        //THEN
        assertEquals("Класс стратегии", ApiKeyPushStrategy.class, result.getClass());
        assertEquals("Ключ фиксации", "TEST_API_KEY", ((ApiKeyPushStrategy) result).getApiKey());
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
        PushTrigger pushTrigger = sourceFactory.createPushTrigger(triggerOptions);
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
     * поле типа boolean
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateSimplePushStrategy() throws Exception {
        //GIVEN
        PackageSourceFactory sourceFactory = new PackageSourceFactory();
        final PushStrategyOptions pushStrategyOptions = new PushStrategyOptions();
        pushStrategyOptions.setClassName(SimplePushStrategy.class.getCanonicalName());
        pushStrategyOptions.getProperties().put("allow", "true");
        //WHEN
        PushStrategy result = sourceFactory.createPushStrategy(pushStrategyOptions);
        //THEN
        assertEquals("Класс стратегии", SimplePushStrategy.class, result.getClass());
        assertTrue("Фиксация разрешена", ((SimplePushStrategy) result).isAllow());
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
        beforeTriggerOptions.setClassName(RemoveOldVersionTrigger.class.getCanonicalName());
        beforeTriggerOptions.getProperties().put("maxPackageCount", "15");
        //Стратегия фиксации
        final PushStrategyOptions pushStrategyOptions = new PushStrategyOptions();
        pushStrategyOptions.setClassName(SimplePushStrategy.class.getCanonicalName());
        pushStrategyOptions.getAftherTriggersOptions().add(aftherTriggerOptions);
        pushStrategyOptions.getBeforeTriggersOptions().add(beforeTriggerOptions);
        //WHEN
        PushStrategy result = sourceFactory.createPushStrategy(pushStrategyOptions);
        //THEN
        assertThat("Стратегия фиксации", result, instanceOf(PushStrategy.class));
        assertThat("Количество созданых before тригеров", result.getBeforeTriggers().size(), equalTo(1));
        assertThat("Количество созданых afther тригеров", result.getAftherTriggers().size(), equalTo(1));
        assertThat("Триггер before", result.getBeforeTriggers().get(0), instanceOf(RemoveOldVersionTrigger.class));
        RemoveOldVersionTrigger beforeTrigger = (RemoveOldVersionTrigger) result.getBeforeTriggers().get(0);
        assertThat("Количество пакетов триггера before ", beforeTrigger.getMaxPackageCount(), equalTo(15));
        assertThat("Триггер afther", result.getAftherTriggers().get(0), instanceOf(RemoveOldVersionTrigger.class));
        RemoveOldVersionTrigger aftherTrigger = (RemoveOldVersionTrigger) result.getAftherTriggers().get(0);
        assertThat("Количество пакетов триггера afther ", aftherTrigger.getMaxPackageCount(), equalTo(5));
    }

    /**
     * Создание корневого хранилища
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateRootPackageSource() throws Exception {
        //GIVEN
        PackageSourceFactory sourceFactory = new PackageSourceFactory();
        Options options = new Options();
        final PushStrategyOptions pushStrategyOptions = new PushStrategyOptions();
        pushStrategyOptions.setClassName(ApiKeyPushStrategy.class.getCanonicalName());
        pushStrategyOptions.getProperties().put("apiKey", "TEST_API_KEY");
        options.setStrategyOptions(pushStrategyOptions);
        //WHEN
        PackageSource result = sourceFactory.createRootPackageSource(options);
        //THEN
        assertEquals("Класс стратегии", ApiKeyPushStrategy.class, result.getPushStrategy().getClass());
        assertEquals("Ключ фиксации", "TEST_API_KEY", ((ApiKeyPushStrategy) result.getPushStrategy()).getApiKey());
    }
}

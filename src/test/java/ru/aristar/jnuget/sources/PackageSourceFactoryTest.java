package ru.aristar.jnuget.sources;

import java.io.File;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.aristar.jnuget.Common.PushStrategyOptions;
import ru.aristar.jnuget.Common.StorageOptions;

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
        StorageOptions storageOptions = new StorageOptions();
        storageOptions.setClassName(FilePackageSource.class.getCanonicalName());
        final PushStrategyOptions pushStrategyOptions = new PushStrategyOptions();
        pushStrategyOptions.setClassName(ApiKeyPushStrategy.class.getCanonicalName());
        pushStrategyOptions.getProperties().put("apiKey", "TEST_API_KEY");
        storageOptions.setStrategyOptions(pushStrategyOptions);
        //WHEN
        PackageSource result = sourceFactory.createPackageSource(storageOptions);
        //THEN
        assertEquals("Класс стратегии", ApiKeyPushStrategy.class, result.getPushStrategy().getClass());
        assertEquals("Ключ фиксации", "TEST_API_KEY", ((ApiKeyPushStrategy) result.getPushStrategy()).getApiKey());
    }
}

package ru.aristar.jnuget.sources;

import java.io.File;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
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
}

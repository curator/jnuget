package ru.aristar.jnuget.files;

import java.io.InputStream;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class TempNupkgFileTest {

    /**
     * Проверка того, что у полученного фала может быть вычеслен хеш
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testHashTempFile() throws Exception {
        //GIVEN
        try (InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg")) {
            //WHEN
            TempNupkgFile nupkgFile = new TempNupkgFile(inputStream);
            //THEN
            assertEquals("Хеш файла, созданного из потока", "kDPZtMu1BOZerHZvsbPnj7"
                    + "DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZj"
                    + "PRJA==", nupkgFile.getHash().toString());
        }
    }

    /**
     * Проверка чтения спецификации
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetNuspecTmpFile() throws Exception {
        //GIVEN
        try (InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg")) {
            //WHEN
            TempNupkgFile nupkgFile = new TempNupkgFile(inputStream);
            NuspecFile nuspecFile = nupkgFile.getNuspecFile();
            //THEN
            assertNotNull("Спецификация пакета", nuspecFile);
            assertEquals("Описание пакета", "Пакет модульного тестирования", nuspecFile.getDescription());
            assertEquals("Идентификатор пакета", "NUnit", nuspecFile.getId());
            assertEquals("Версия пакета", Version.parse("2.5.9.10348"), nuspecFile.getVersion());
        }
    }

    /**
     * Тест чтения имени файла из временного пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetFileName() throws Exception {
        //GIVEN
        try (InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/NUnit.2.5.9.10348.nupkg")) {
            //WHEN
            Nupkg nupkgFile = new TempNupkgFile(inputStream, new Date());
            //THEN
            assertEquals("Имя файла", "NUnit.2.5.9.10348.nupkg", nupkgFile.getFileName());
        }
    }
}

package ru.aristar.jnuget.files;

import java.io.InputStream;
import static org.junit.Assert.*;
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
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        //WHEN
        TempNupkgFile nupkgFile = new TempNupkgFile(inputStream);
        //THEN
        assertEquals("Хеш файла, созданного из потока", "kDPZtMu1BOZerHZvsbPnj7"
                + "DfOdEyn/j4fanlv7BWuuVOZ0+VwuuxWzUnpD7jo7pkLjFOqIs41Vkk7abFZj"
                + "PRJA==", nupkgFile.getHash().toString());
    }

    /**
     * Проверка чтения спецификации
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetNuspecTmpFile() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        //WHEN
        TempNupkgFile nupkgFile = new TempNupkgFile(inputStream);
        //THEN
        assertNotNull("Спецификация пакета", nupkgFile.getNuspecFile());
        assertEquals("Идентификатор пакета", "NUnit", nupkgFile.getNuspecFile().getId());
        assertEquals("Версия пакета", Version.parse("2.5.9.10348"), nupkgFile.getNuspecFile().getVersion());
    }
}

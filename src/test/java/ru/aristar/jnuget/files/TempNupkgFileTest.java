package ru.aristar.jnuget.files;

import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
                + "PRJA==", nupkgFile.getHash());
    }
}

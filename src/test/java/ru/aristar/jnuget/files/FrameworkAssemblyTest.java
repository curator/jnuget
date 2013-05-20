package ru.aristar.jnuget.files;

import java.util.EnumSet;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Тесты коллекции сборок, входящих в поставку Frameworks
 *
 * @author sviridov
 */
public class FrameworkAssemblyTest {

    /**
     * Проверка преобразования пустой строки
     *
     * @throws Exception ошибка преобразования
     */
    @Test
    public void testUnmarshalEmptyValue() throws Exception {
        //GIVEN
        FrameworkAssembly.AssemblyTargetFrameworkAdapter adapter = new FrameworkAssembly.AssemblyTargetFrameworkAdapter();
        //WHEN
        EnumSet<Framework> result = adapter.unmarshal("");
        //THEN
        assertThat(result, is(nullValue()));
    }

    /**
     * Проверка преобразования null значения
     *
     * @throws Exception ошибка преобразования
     */
    @Test
    public void testMarshalNullValue() throws Exception {
        //GIVEN
        FrameworkAssembly.AssemblyTargetFrameworkAdapter adapter = new FrameworkAssembly.AssemblyTargetFrameworkAdapter();
        //WHEN
        String result = adapter.marshal(null);
        //THEN
        assertThat(result, is(nullValue()));
    }
}
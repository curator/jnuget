package ru.aristar.jnuget.Common;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class OptionConverterTest {

    /**
     * Проверка подстановки значений вместо служебных переменных
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testReplaceVariables() throws Exception {
        //GIVEN
        OptionConverter.putValue("test-properties", "test-value");
        //WHEN
        String result = OptionConverter.replaceVariables("value = ${test-properties}/.nuget ${test-properties} aefasdf");
        //THEN
        assertEquals("Строка с замененными свойствами", "value = test-value/.nuget test-value aefasdf", result);
    }

    /**
     * Проверка подстановки значений системных переменных
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testReplaceSystemVariables() throws Exception {
        //GIVEN
        String userHome = System.getProperty("user.home");
        //WHEN
        String result = OptionConverter.replaceVariables("${user.home}/.nuget");
        //THEN
        assertEquals("Строка с замененными свойствами", userHome + "/.nuget", result);
    }
}

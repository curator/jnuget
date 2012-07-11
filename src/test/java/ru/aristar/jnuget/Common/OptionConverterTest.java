package ru.aristar.jnuget.Common;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class OptionConverterTest {

    /**
     * Проверка подстановки значений вместо служебных переменных
     */
    @Test
    public void testReplaceVariables() {
        //GIVEN
        OptionConverter.putValue("test-properties", "test-value");
        //WHEN
        String result = OptionConverter.replaceVariables("value = ${test-properties}/.nuget ${test-properties} aefasdf");
        //THEN
        assertThat(result, is(equalTo("value = test-value/.nuget test-value aefasdf")));
    }

    /**
     * Проверка подстановки значений системных переменных
     */
    @Test
    public void testReplaceSystemVariables() {
        //GIVEN
        String userHome = System.getProperty("user.home");
        //WHEN
        String result = OptionConverter.replaceVariables("${user.home}/.nuget");
        //THEN
        assertThat(result, is(equalTo(userHome + "/.nuget")));
    }

    /**
     * Проверка замены переменных локализации
     */
    @Test
    public void testReplaceLoclizationVariables() {
        //WHEN
        String result = OptionConverter.replaceVariables("${test.value}", "ui.localization.TestBundle");
        //THEN
        assertThat(result, is(equalTo("Тестовый текст")));
    }
}

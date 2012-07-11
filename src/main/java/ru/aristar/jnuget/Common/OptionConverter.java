package ru.aristar.jnuget.Common;

import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sviridov
 */
public class OptionConverter {

    /**
     * Системные настройки
     */
    private static Properties properties = new Properties(System.getProperties());

    /**
     * Заменяет элементы, помеченые маркерными символами на их значения свойств
     * <B>${</B> открывает тег, <B>}</B> закрывает. Например <B>${user.home}</B>
     *
     * @param value исходная строка
     * @return строка с измененными значениями
     */
    public static String replaceVariables(String value) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(value);
        StringBuilder builder = new StringBuilder();
        int prevEnd = 0;
        while (matcher.find()) {
            int currStart = matcher.start();
            builder.append(value.substring(prevEnd, currStart));
            prevEnd = matcher.end();
            String key = matcher.group(1);
            builder.append(getValue(key));
        }
        builder.append(value.substring(prevEnd, value.length()));
        return builder.toString();
    }

    /**
     * Заменяет элементы, помеченые маркерными символами на их значения свойств
     * <B>${</B> открывает тег, <B>}</B> закрывает. Например <B>${user.home}</B>
     * свойства получаются из ресурса, по указанному имени
     *
     * @param value исходная строка
     * @param bundleName имя ресурса со значениями
     * @return строка с измененными значениями
     */
    public static String replaceVariables(String value, String bundleName) {
        //TODO Обработать без Exeption
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName);
            Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher matcher = pattern.matcher(value);
            StringBuilder builder = new StringBuilder();
            int prevEnd = 0;
            while (matcher.find()) {
                int currStart = matcher.start();
                builder.append(value.substring(prevEnd, currStart));
                prevEnd = matcher.end();
                String key = matcher.group(1);
                builder.append(resourceBundle.getString(key));
            }
            builder.append(value.substring(prevEnd, value.length()));
            return builder.toString();
        } catch (MissingResourceException e) {
            return value;
        }
    }

    /**
     * Возвращает значение для указанного свойства
     *
     * @param key свойство
     * @return значение
     */
    public static String getValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * Устанавливает свойству значение
     *
     * @param key свойство
     * @param value значение
     */
    public static void putValue(String key, String value) {
        properties.setProperty(key, value);
    }
}

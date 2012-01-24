package ru.aristar.jnuget.Common;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sviridov
 */
public class OptionConverter {

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

    public static String getValue(String key) {
        return properties.getProperty(key);
    }

    public static void putValue(String key, String value) {
        properties.setProperty(key, value);
    }
}

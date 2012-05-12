package ru.aristar.jnuget.files;

import java.util.EnumSet;

/**
 * Фреймворки
 *
 * @author sviridov
 */
public enum Framework {

    /**
     * NET 4.0
     */
    net40,
    /**
     * NET 3.5
     */
    net35,
    /**
     * NET 2.0
     */
    net20,
    /**
     * NET 1.0
     */
    net10,
    /**
     * SilverLight 4
     */
    sl4,
    /**
     * SilverLight 5
     */
    sl5;
    /**
     * Разделитель фреймворков в строке запроса
     */
    public static final String QUERY_STRING_DELIMETER = "\\|";

    /**
     * Извлечение списка фреймворков из строки запроса
     *
     * @param value строка запроса
     * @return список фреймворков
     */
    public static EnumSet<Framework> parse(String value) {
        EnumSet<Framework> result = null;
        if (value != null && !value.isEmpty()) {
            result = EnumSet.noneOf(Framework.class);
            String[] frameworkStrings = value.split(QUERY_STRING_DELIMETER);
            for (String frameworkString : frameworkStrings) {
                Framework framework = Framework.valueOf(frameworkString.toLowerCase());
                result.add(framework);
            }
        } else {
            result = EnumSet.allOf(Framework.class);
        }
        return result;
    }
}

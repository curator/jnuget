package ru.aristar.jnuget.files;

import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Фреймворки
 *
 * @author sviridov
 */
public enum Framework {

    /**
     * NET 1.0
     */
    net10,
    /**
     * NET 1.1
     */
    net11("net10"),
    /**
     * NET 2.0
     */
    net20(),
    /**
     * NET 3.5
     */
    net35("net20"),
    /**
     * NET 4.0
     */
    net40("net35", "net20"),
    /**
     * SilverLight 4
     */
    sl4,
    /**
     * SilverLight 5
     */
    sl5;

    /**
     * @param copabilityFrameworks фреймворки совместимые с данным
     */
    private Framework(String... copabilityFrameworks) {
        fullCopabilyStringSet = copabilityFrameworks;
    }
    /**
     * Набор названий фреймворков совместимых с данным
     */
    private final String[] fullCopabilyStringSet;
    /**
     * Набор фреймворков совместимых с данным
     */
    private volatile EnumSet<Framework> fullCopabilySet;

    /**
     * @return набор фреймворков совместимых с данным
     */
    public EnumSet<Framework> getFullCopabilySet() {
        if (fullCopabilySet == null) {
            synchronized (this) {
                if (fullCopabilySet == null) {
                    fullCopabilySet = EnumSet.noneOf(Framework.class);
                    fullCopabilySet.add(this);
                    for (String frameworkName : fullCopabilyStringSet) {
                        fullCopabilySet.add(Framework.valueOf(frameworkName));
                    }
                }
            }
        }
        return fullCopabilySet;
    }
    /**
     * Логгер
     */
    private static Logger logger = LoggerFactory.getLogger(Framework.class);
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
        EnumSet<Framework> result;
        try {
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
        } catch (IllegalArgumentException e) {
            logger.warn("Не определен фреймворк для строки '" + value
                    + "' используется фреймворк по умолчанию", e);
            result = EnumSet.allOf(Framework.class);
        }
        
        return result;
    }
}
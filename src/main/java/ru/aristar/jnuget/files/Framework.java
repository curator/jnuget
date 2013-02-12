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
    net10(".NETFramework1.0", new String[]{"net10"}, new String[]{}),
    /**
     * NET 1.1
     */
    net11(".NETFramework1.1", new String[]{"net11"}, new String[]{"net10"}),
    /**
     * NET 2.0
     */
    net20(".NETFramework2.0", new String[]{"net20"}, new String[]{}),
    /**
     * NET 3.0
     */
    net30(".NETFramework3.0", new String[]{"net30"}, new String[]{"net20"}),
    /**
     * NET 3.5
     */
    net35(".NETFramework3.5", new String[]{"net35"}, new String[]{"net30", "net20"}),
    /**
     * NET 4.0
     */
    net40(".NETFramework4.0", new String[]{"net40"}, new String[]{"net35", "net30", "net20"}),
    /**
     * NET 4.5
     */
    net45(".NETFramework4.5", new String[]{"net45", "win80"}, new String[]{"net40", "net35", "net30", "net20"}),
    /**
     * .NETFramework4.5 Portable
     */
    portable_net45(".NETFramework4.5 Portable", new String[]{"portable-net45"}, new String[]{}),
    /**
     * WinRT 4.5
     */
    winrt45("WinRT 4.5", new String[]{"winrt45"}, new String[]{}),
    /**
     * SilverLight 2.0
     */
    sl20("SilverLight 2", new String[]{"sl2"}, new String[]{}),
    /**
     * SilverLight 30
     */
    sl30("SilverLight 30", new String[]{"sl30", "sl3"}, new String[]{}),
    /**
     * SilverLight 4
     */
    sl4("SilverLight 4", new String[]{"sl4", "sl40", "sl40-wp71"}, new String[]{}),
    /**
     * SilverLight 5
     */
    sl5("SilverLight 5", new String[]{"sl5", "sl50"}, new String[]{}),
    /**
     * WindowsPhone 7.1
     */
    wp71("WindowsPhone 7.1", new String[]{"wp71"}, new String[]{}),
    /**
     * WindowsPhone 8.0
     */
    wp80("WindowsPhone 8", new String[]{"wp80"}, new String[]{});

    /**
     * @param fullName полное название фреймворка
     * @param shortNames допустимые краткие имена фреймворка
     * @param copabilityFrameworks фреймворки совместимые с данным
     */
    private Framework(String fullName, String[] shortNames, String[] copabilityFrameworks) {
        this.shortNames = shortNames;
        this.fullName = fullName;
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
     * Полное название фреймворка
     */
    private final String fullName;
    /**
     * Краткое название фреймворка
     */
    private final String[] shortNames;

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
     * @return полное название фреймворка
     */
    public String getFullName() {
        return this.fullName;
    }

    /**
     * @return краткое имя фреймворка
     */
    public String getShortName() {
        return shortNames[0];
    }
    /**
     * Логгер
     */
    private static Logger logger = LoggerFactory.getLogger(Framework.class);
    /**
     * Разделитель фреймворков в строке запроса
     */
    public static final String QUERY_STRING_DELIMETER = "\\||\\+";

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
                    Framework framework = getByShortName(frameworkString.toLowerCase());
                    if (framework == null) {
                        logger.warn("Не найден фреймворк для строки: {}", new Object[]{frameworkString});
                        continue;
                    }
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

    /**
     * Поиск фреймворка по полному имени
     *
     * @param fullName полное имя фреймворка
     * @return фреймворк или null, если ничего не найдено
     */
    public static Framework getByFullName(String fullName) {
        for (Framework framework : values()) {
            if (framework.getFullName().equalsIgnoreCase(fullName)) {
                return framework;
            }
        }
        return null;
    }

    /**
     * Поиск фреймворка по краткому имени
     *
     * @param shortName короткое имя фреймворка
     * @return фреймворк
     */
    public static Framework getByShortName(String shortName) {
        for (Framework framework : values()) {
            for (String name : framework.shortNames) {
                if (name.equalsIgnoreCase(shortName)) {
                    return framework;
                }
            }
        }
        return null;
    }
}

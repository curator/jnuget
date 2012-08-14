package ru.aristar.jnuget.ui.descriptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.text.MessageFormat.format;
import ru.aristar.jnuget.common.OptionConverter;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Свойство объекта
 *
 * @author sviridov
 */
public class ObjectProperty {

    /**
     * Описание свойства
     */
    private final String description;
    /**
     * Метод, позволяющий получить значение свойства
     */
    private final Method getter;
    /**
     * Метод, позволяющий установить значение свойства
     */
    private final Method setter;
    /**
     * Уникальное имя свойства
     */
    private final String name;
    /**
     * Класс, для свойства которого создан дескриптор
     */
    private final Class<?> type;

    /**
     * Возвращает строку, у которой первый символ переведен в верхний регистр
     *
     * @param value исходная строка
     * @return преобразованная строка
     */
    private String upperFirstSymbol(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * Возвращает имя get метода для указанного свойства
     *
     * @param propertyName имя свойства
     * @param propertyType тип свойства
     * @return имя get метода
     */
    private String getGetterName(String propertyName, Class<?> propertyType) {
        if (propertyType != null && (propertyType.equals(Boolean.class) || propertyType.equals(Boolean.TYPE))) {
            return "is" + upperFirstSymbol(propertyName);
        } else {
            return "get" + upperFirstSymbol(propertyName);
        }
    }

    /**
     * Возвращает имя set метода для указанного свойства
     *
     * @param propertyName имя свойства
     * @return имя set метода
     */
    private String getSetterName(String propertyName) {
        return "set" + upperFirstSymbol(propertyName);
    }

    /**
     * @param ownerType тип объекта для которого предназначено свойство
     * @param propertyType тип свойства
     * @param description описание свойства
     * @param name уникальное имя свойства
     * @throws NoSuchMethodException не найден сеттер
     */
    public ObjectProperty(Class<?> ownerType, Class<?> propertyType, String description, String name)
            throws NoSuchMethodException {
        this.description = description;
        this.name = name;
        this.getter = ownerType.getMethod(getGetterName(name, propertyType));
        this.type = ownerType;
        this.setter = findSetter(ownerType, getSetterName(name));
    }

    /**
     * @return описание свойства
     */
    public String getDescription() {
        return OptionConverter.replaceVariables(description, type.getName());
    }

    /**
     * Находит сетер в классе
     *
     * @param type класс, в котором производится поиск сеттера
     * @param setterName имя метода сеттера
     * @return сеттер
     * @throws NoSuchMethodException не найден сеттер
     */
    private Method findSetter(Class<?> type, String setterName) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        throw new NoSuchMethodException(format("Метод {0} не найден в классе {1}", setterName, type.getName()));
    }

    /**
     * @param object объект, у которого нужно получить значение свойства
     * @return значение свойства
     */
    public String getValue(Object object) {
        try {
            return getter.invoke(object).toString();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return "";
        }
    }

    /**
     * @param object объект, которому нужно установить значение свойства
     * @param stringValue строковое значение свойства
     * @throws Exception ошибка установки свойства
     */
    public void setValue(Object object, String stringValue) throws Exception {
        Class<?> valueType = setter.getParameterTypes()[0];
        stringValue = OptionConverter.replaceVariables(stringValue);
        Object value = PackageSourceFactory.getValueFromString(valueType, stringValue);
        setter.invoke(object, value);
    }

    /**
     * @return уникальное имя свойства
     */
    public String getName() {
        return name;
    }
}

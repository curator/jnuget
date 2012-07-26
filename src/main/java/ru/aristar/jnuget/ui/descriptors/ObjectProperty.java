package ru.aristar.jnuget.ui.descriptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.text.MessageFormat.format;
import java.util.ResourceBundle;
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
     * Класс, для свойства которого создан дескриптор
     */
    private final Class<?> type;

    /**
     * @param type тип объекта для которого предназначено свойство
     * @param description описание свойство
     * @param getterName имя метода геттера
     * @param setterName имя метода сеттера
     * @throws NoSuchMethodException не найден сеттер
     */
    public ObjectProperty(Class<?> type, String description, String getterName, String setterName)
            throws NoSuchMethodException {
        this.description = description;
        this.getter = type.getMethod(getterName);
        this.type = type;
        this.setter = findSetter(type, setterName);
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
}

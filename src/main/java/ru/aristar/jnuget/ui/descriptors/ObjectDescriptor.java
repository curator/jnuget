package ru.aristar.jnuget.ui.descriptors;

import java.util.List;

/**
 * Дескриптор класса для редактирования его свойств через UI
 *
 * @param <T> класс, который описывает дескриптор
 * @author sviridov
 */
public interface ObjectDescriptor<T> {

    /**
     * @return класс, который описывает дескриптор
     */
    Class<? extends T> getObjectClass();

    /**
     * @return список свойств класа
     */
    public List<ObjectProperty> getProperties();
}

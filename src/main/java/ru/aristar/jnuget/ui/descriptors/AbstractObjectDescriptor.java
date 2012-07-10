package ru.aristar.jnuget.ui.descriptors;

import java.util.List;

/**
 * Описание произвольного класса
 *
 * @author sviridov
 */
public class AbstractObjectDescriptor<T> implements ObjectDescriptor<T> {

    /**
     * Описываемый класс
     */
    private final Class<? extends T> c;
    /**
     * Список описываемых свойств
     */
    private final List<ObjectProperty> propertys;

    /**
     * @param c класс, для которого создается описание
     * @param propertys список описываемых свойств
     */
    public AbstractObjectDescriptor(Class<? extends T> c, List<ObjectProperty> propertys) {
        this.c = c;
        this.propertys = propertys;
    }

    @Override
    public Class<? extends T> getObjectClass() {
        return c;
    }

    @Override
    public List<ObjectProperty> getProperties() {
        return propertys;
    }
}

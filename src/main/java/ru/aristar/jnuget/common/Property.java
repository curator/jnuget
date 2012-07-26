package ru.aristar.jnuget.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Свойство класса
 *
 * @author sviridov
 */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.NONE)
public class Property {

    /**
     * Имя свойства
     */
    @XmlAttribute(name = "name")
    public String name;
    /**
     * Строковое значение свойства
     */
    @XmlAttribute(name = "value")
    public String value;

    /**
     * Конструктор по умолчанию
     */
    public Property() {
    }

    /**
     * @param name имя свойства
     * @param value строковое значение свойства
     */
    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }
}

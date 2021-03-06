package ru.aristar.jnuget.common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Свойства хранилища
 *
 * @author sviridov
 */
@XmlRootElement(name = "Properties")
@XmlAccessorType(XmlAccessType.NONE)
public class Properties {

    /**
     * Список свойств
     */
    @XmlElement(name = "property")
    public List<Property> properties;

    /**
     * @return список свойств (не может быть null)
     */
    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        return properties;
    }

    /**
     * @param properties список свойств
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}

package ru.aristar.jnuget.Common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sviridov
 */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.NONE)
public class Property {

    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "value")
    public String value;

    public Property() {
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }
}

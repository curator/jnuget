package ru.aristar.jnuget;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "author")
@XmlAccessorType(XmlAccessType.NONE)
public class Author {

    @XmlElement(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }
}

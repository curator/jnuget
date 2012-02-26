package ru.aristar.jnuget.rss;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "title", namespace = PackageFeed.ATOM_XML_NAMESPACE)
public class Title {

    @XmlAttribute(name = "type")
    private String type = "text";
    @XmlValue
    public String value;

    public Title() {
    }

    public Title(String value) {
        this.value = value;
    }
}

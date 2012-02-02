package ru.aristar.jnuget.Common;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author sviridov
 */
@XmlRootElement(name = "storage")
@XmlAccessorType(XmlAccessType.NONE)
public class StorageOptions {

    @XmlAttribute(name = "class")
    private String className;
    @XmlJavaTypeAdapter(PropertiesTypeAdapter.class)
    @XmlElement(name = "properties")
    private Map<String, String> properties;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, String> getProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
        return properties;
    }
}

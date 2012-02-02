package ru.aristar.jnuget.Common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author sviridov
 */
@XmlRootElement(name = "Properties")
@XmlAccessorType(XmlAccessType.NONE)
public class Properties {

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public List<Property> properties;

    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}

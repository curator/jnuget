package ru.aristar.jnuget.rss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author sviridov
 */
@XmlAccessorType(XmlAccessType.NONE)
public class MicrosoftDatasetElement {

    //xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata"   
    //<d:Tags xml:space="preserve"> Unit test </d:Tags>
    @XmlAttribute(name = "null", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
    private Boolean nullable;
    @XmlAttribute(name = "type", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
    private MicrosoftTypes type;
    @XmlValue
    private String value;

    public MicrosoftDatasetElement() {
    }

    public MicrosoftDatasetElement(Boolean nullable, MicrosoftTypes type, String value) {
        this.nullable = nullable;
        this.type = type;
        this.value = value;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public MicrosoftTypes getType() {
        return type;
    }

    public void setType(MicrosoftTypes type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

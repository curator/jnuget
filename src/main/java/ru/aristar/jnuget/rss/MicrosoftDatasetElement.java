package ru.aristar.jnuget.rss;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author sviridov
 */
@XmlAccessorType(XmlAccessType.NONE)
public class MicrosoftDatasetElement {

    //xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata"   
    //<d:Tags xml:space="preserve"> Unit test </d:Tags>
    /**
     * Может ли поле принимать значение NULL
     */
    @XmlAttribute(name = "null", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
    private Boolean nullable;
    /**
     * Тип поля по версии Microsoft
     */
    @XmlAttribute(name = "type", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
    @XmlJavaTypeAdapter(MicrosoftTypeAdapter.class)
    private MicrosoftTypes type;
    /**
     * Значение поля
     */
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MicrosoftDatasetElement other = (MicrosoftDatasetElement) obj;
        if (!Objects.equals(this.nullable, other.nullable)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.nullable);
        hash = 23 * hash + Objects.hashCode(this.type);
        hash = 23 * hash + Objects.hashCode(this.value);
        return hash;
    }
}

package ru.aristar.jnuget.rss;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author sviridov
 */
public class AtomElement {

    @XmlAttribute
    private String term;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String scheme;
    @XmlAttribute
    private String src;

    public AtomElement() {
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package ru.aristar.jnuget.rss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sviridov
 */
@XmlRootElement(name = "link", namespace = PackageFeed.ATOM_XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
class Link {

    @XmlAttribute(name = "rel")
    private String rel;
    @XmlAttribute(name = "title")
    private String title;
    @XmlAttribute(name = "href")
    private String href;

    public Link() {
    }

    public Link(String rel, String title) {
        this.rel = rel;
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

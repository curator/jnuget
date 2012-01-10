package ru.aristar.jnuget.rss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author Unlocker
 */
@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.NONE)
public class PackageEntry {

    @XmlElement(name = "id")
    private String id;

    public PackageEntry() {
    }

    public PackageEntry(NupkgFile nupkgFile) {
        this(nupkgFile.getNuspecFile());
    }

    public PackageEntry(NuspecFile nuspecFile) {
        throw new UnsupportedOperationException("Конструктор не реализован");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

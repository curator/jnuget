package ru.aristar.jnuget.sources;

import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class NugetPackageId {

    private String id;
    private Version version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}

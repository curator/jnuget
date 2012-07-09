package ru.aristar.jnuget.ui.descriptors;

import java.util.List;
import ru.aristar.jnuget.sources.MavenStylePackageSource;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class MavenStylePackageSourceDescriptor implements PackageSourceDescriptor {

    @Override
    public Class<? extends PackageSource> getPackageSourceClass() {
        return MavenStylePackageSource.class;
    }

    @Override
    public List<ObjectProperty> getProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

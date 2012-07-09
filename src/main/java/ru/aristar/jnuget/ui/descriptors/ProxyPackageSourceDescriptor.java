package ru.aristar.jnuget.ui.descriptors;

import java.util.List;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.ProxyPackageSource;

/**
 *
 * @author sviridov
 */
public class ProxyPackageSourceDescriptor implements PackageSourceDescriptor {

    @Override
    public Class<? extends PackageSource> getPackageSourceClass() {
        return ProxyPackageSource.class;
    }

    @Override
    public List<ObjectProperty> getProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

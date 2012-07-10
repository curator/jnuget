package ru.aristar.jnuget.ui.descriptors;

import java.util.List;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.ProxyPackageSource;

/**
 *
 * @author sviridov
 */
public class ProxyPackageSourceDescriptor implements ObjectDescriptor {

    @Override
    public Class<? extends PackageSource> getObjectClass() {
        return ProxyPackageSource.class;
    }

    @Override
    public List<ObjectProperty> getProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

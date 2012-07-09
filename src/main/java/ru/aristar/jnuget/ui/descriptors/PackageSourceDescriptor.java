package ru.aristar.jnuget.ui.descriptors;

import java.util.List;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Дескриптор хранилища для редактирования его свойств через UI
 *
 * @author sviridov
 */
public interface PackageSourceDescriptor {

    Class<? extends PackageSource> getPackageSourceClass();

    public List<ObjectProperty> getProperties();
}

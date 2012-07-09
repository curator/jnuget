package ru.aristar.jnuget.ui.descriptors;

import java.util.ArrayList;
import java.util.List;
import ru.aristar.jnuget.sources.ClassicPackageSource;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class ClassicPackageSourceDescriptor implements PackageSourceDescriptor {

    @Override
    public Class<? extends PackageSource> getPackageSourceClass() {
        return ClassicPackageSource.class;
    }

    @Override
    public List<ObjectProperty> getProperties() {
        ArrayList<ObjectProperty> result = new ArrayList<>();
        ObjectProperty property = new ObjectProperty();
        property.description = "Каталог, в котором будут храниться пакеты";
        result.add(property);
        return result;
    }
}

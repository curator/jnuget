package ru.aristar.jnuget.ui.descriptors;

import java.util.ArrayList;
import java.util.List;
import ru.aristar.jnuget.sources.ClassicPackageSource;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Описание свойств для классического хранилища NuGet
 *
 * @see ClassicPackageSource
 * @author sviridov
 */
public class ClassicPackageSourceDescriptor implements ObjectDescriptor {

    @Override
    public Class<? extends PackageSource> getObjectClass() {
        return ClassicPackageSource.class;
    }

    @Override
    public List<ObjectProperty> getProperties() {
        ArrayList<ObjectProperty> result = new ArrayList<>();
        try {
            ObjectProperty property = new ObjectProperty(
                    ClassicPackageSource.class,
                    "Каталог, в котором будут храниться пакеты",
                    "getFolderName",
                    "setFolderName");
            result.add(property);
        } catch (NoSuchMethodException e) {
        }
        return result;
    }
}

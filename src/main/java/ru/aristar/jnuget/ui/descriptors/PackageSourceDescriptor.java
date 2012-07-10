package ru.aristar.jnuget.ui.descriptors;

import java.util.List;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Дескриптор хранилища для редактирования его свойств через UI
 *
 * @author sviridov
 */
public interface PackageSourceDescriptor {

    /**
     * @return класс хранилища пакетов
     */
    Class<? extends PackageSource> getPackageSourceClass();

    /**
     * @return список свойств хранилища
     */
    public List<ObjectProperty> getProperties();
}

package ru.aristar.jnuget.ui.descriptors;

import java.lang.reflect.Method;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class ObjectProperty<T extends PackageSource> {

    public String description;
    public Class<?> type;
    public Method getter;
    public Method setter;

    public String getValue(T packageSource) {
        return "";
    }
}

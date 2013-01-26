package ru.aristar.jnuget.query;

import java.util.Collection;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * @author sviridov
 */
public class EmptyExpression implements Expression {

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        return packageSource.getPackages();
    }

    @Override
    public Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages) {
        return packages;
    }

    @Override
    public boolean hasFilterPriority() {
        return true;
    }
}

package ru.aristar.jnuget.query;

import java.util.Collection;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class LatestVersionExpression implements Expression {

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        return packageSource.getLastVersionPackages();
    }
}

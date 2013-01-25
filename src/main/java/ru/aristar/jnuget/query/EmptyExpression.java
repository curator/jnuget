package ru.aristar.jnuget.query;

import java.util.ArrayList;
import java.util.Collection;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * @author sviridov
 */
public class EmptyExpression implements Expression {

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        return new ArrayList<>();
    }
}

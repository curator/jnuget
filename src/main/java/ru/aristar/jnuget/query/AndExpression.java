package ru.aristar.jnuget.query;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class AndExpression implements Expression {

    public Expression firstExpression;
    public Expression secondExpression;

    @Override
    public List<Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
}

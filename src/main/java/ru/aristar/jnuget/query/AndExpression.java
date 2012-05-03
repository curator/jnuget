package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashSet;
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
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        HashSet<Nupkg> result = new HashSet<>();
        Collection<? extends Nupkg> firstExpressionResult = firstExpression.execute(packageSource);
        result.addAll(firstExpressionResult);
        Collection<? extends Nupkg> secondExpressionResult = secondExpression.execute(packageSource);
        result.retainAll(secondExpressionResult);
        return result;
    }
}

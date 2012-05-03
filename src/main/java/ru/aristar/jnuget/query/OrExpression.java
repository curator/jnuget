package ru.aristar.jnuget.query;

import java.util.ArrayList;
import java.util.Collection;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class OrExpression implements Expression {

    public Expression firstExpression;
    public Expression secondExpression;

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        ArrayList<Nupkg> result = new ArrayList<>();
        Collection<? extends Nupkg> firstExpressionResult = firstExpression.execute(packageSource);
        result.addAll(firstExpressionResult);
        Collection<? extends Nupkg> secondExpressionResult = secondExpression.execute(packageSource);
        result.addAll(secondExpressionResult);
        return result;
    }
}

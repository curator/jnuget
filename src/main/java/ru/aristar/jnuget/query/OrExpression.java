package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashSet;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Логическое сложение
 *
 * @author sviridov
 */
public class OrExpression extends BinaryExpression {

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        HashSet<Nupkg> result = new HashSet<>();
        Collection<? extends Nupkg> firstExpressionResult = getFirstExpression().execute(packageSource);
        result.addAll(firstExpressionResult);
        Collection<? extends Nupkg> secondExpressionResult = getSecondExpression().execute(packageSource);
        result.addAll(secondExpressionResult);
        return result;
    }

    @Override
    public boolean accept(Nupkg nupkg) {
        return getFirstExpression().accept(nupkg) || getSecondExpression().accept(nupkg);
    }
}

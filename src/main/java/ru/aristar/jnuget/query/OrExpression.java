package ru.aristar.jnuget.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Логическое сложение
 *
 * @author sviridov
 */
public class OrExpression implements Expression {

    /**
     * Первое слогаемое
     */
    private Expression firstExpression;
    /**
     * Второе слагаемое
     */
    private Expression secondExpression;

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        HashSet<Nupkg> result = new HashSet<>();
        Collection<? extends Nupkg> firstExpressionResult = getFirstExpression().execute(packageSource);
        result.addAll(firstExpressionResult);
        Collection<? extends Nupkg> secondExpressionResult = getSecondExpression().execute(packageSource);
        result.addAll(secondExpressionResult);
        return result;
    }

    /**
     * @return Первое слогаемое
     */
    public Expression getFirstExpression() {
        return firstExpression;
    }

    /**
     * @param firstExpression Первое слогаемое
     */
    public void setFirstExpression(Expression firstExpression) {
        this.firstExpression = firstExpression;
    }

    /**
     * @return Второе слагаемое
     */
    public Expression getSecondExpression() {
        return secondExpression;
    }

    /**
     * @param secondExpression Второе слагаемое
     */
    public void setSecondExpression(Expression secondExpression) {
        this.secondExpression = secondExpression;
    }

    @Override
    public Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages) {
        HashSet<Nupkg> result = new HashSet<>();
        result.addAll(getFirstExpression().filter(packages));
        result.addAll(getFirstExpression().filter(packages));
        return result;
    }

    @Override
    public boolean hasFilterPriority() {
        return getFirstExpression().hasFilterPriority() && getFirstExpression().hasFilterPriority();
    }
}

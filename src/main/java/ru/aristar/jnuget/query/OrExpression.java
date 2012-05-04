package ru.aristar.jnuget.query;

import java.util.ArrayList;
import java.util.Collection;
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
        ArrayList<Nupkg> result = new ArrayList<>();
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
}

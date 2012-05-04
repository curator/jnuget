package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashSet;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Логическое умножение
 *
 * @author sviridov
 */
public class AndExpression implements Expression {

    /**
     * Первый множитель
     */
    private Expression firstExpression;
    /**
     * Второй множитель
     */
    private Expression secondExpression;

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        HashSet<Nupkg> result = new HashSet<>();
        Collection<? extends Nupkg> firstExpressionResult = getFirstExpression().execute(packageSource);
        result.addAll(firstExpressionResult);
        Collection<? extends Nupkg> secondExpressionResult = getSecondExpression().execute(packageSource);
        result.retainAll(secondExpressionResult);
        return result;
    }

    /**
     * @return Первый множитель
     */
    public Expression getFirstExpression() {
        return firstExpression;
    }

    /**
     * @param firstExpression Первый множитель
     */
    public void setFirstExpression(Expression firstExpression) {
        this.firstExpression = firstExpression;
    }

    /**
     * @return Второй множитель
     */
    public Expression getSecondExpression() {
        return secondExpression;
    }

    /**
     * @param secondExpression Второй множитель
     */
    public void setSecondExpression(Expression secondExpression) {
        this.secondExpression = secondExpression;
    }
}

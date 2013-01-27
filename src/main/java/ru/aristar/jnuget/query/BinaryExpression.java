package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashSet;
import ru.aristar.jnuget.files.Nupkg;

/**
 * Бинарная операция
 *
 * @author sviridov
 */
public abstract class BinaryExpression implements Expression {

    /**
     * Первое слогаемое
     */
    protected Expression firstExpression;
    /**
     * Второе слагаемое
     */
    protected Expression secondExpression;

    @Override
    public Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages) {
        HashSet<Nupkg> result = new HashSet<>();
        for (Nupkg nupkg : result) {
            if (accept(nupkg)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    /**
     * @return Первое слогаемое
     */
    public Expression getFirstExpression() {
        return firstExpression;
    }

    /**
     * @return Второе слагаемое
     */
    public Expression getSecondExpression() {
        return secondExpression;
    }

    /**
     * @param firstExpression Первое слогаемое
     */
    public void setFirstExpression(Expression firstExpression) {
        this.firstExpression = firstExpression;
    }

    /**
     * @param secondExpression Второе слагаемое
     */
    public void setSecondExpression(Expression secondExpression) {
        this.secondExpression = secondExpression;
    }

    @Override
    public boolean hasFilterPriority() {
        return getFirstExpression().hasFilterPriority() && getFirstExpression().hasFilterPriority();
    }
}

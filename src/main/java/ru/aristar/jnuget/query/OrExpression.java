package ru.aristar.jnuget.query;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class OrExpression implements Expression {

    public Expression firstExpression;
    public Expression secondExpression;

    @Override
    public List<Nupkg> execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

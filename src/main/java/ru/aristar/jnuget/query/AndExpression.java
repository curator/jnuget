package ru.aristar.jnuget.query;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class AndExpression implements Expression {

    public Expression firstExpression;
    public Expression secondExpression;

    @Override
    public QueryLexer.Operation getOperation() {
        return QueryLexer.Operation.AND;
    }

    @Override
    public List<Nupkg> execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

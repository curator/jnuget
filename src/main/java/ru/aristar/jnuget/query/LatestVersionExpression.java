package ru.aristar.jnuget.query;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class LatestVersionExpression implements Expression {

    @Override
    public QueryLexer.Operation getOperation() {
        return null;
    }

    @Override
    public List<Nupkg> execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

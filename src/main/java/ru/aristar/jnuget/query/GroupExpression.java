package ru.aristar.jnuget.query;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class GroupExpression implements Expression {

    public Expression innerExpression;

    @Override
    public List<Nupkg> execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

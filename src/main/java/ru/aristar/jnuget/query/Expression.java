package ru.aristar.jnuget.query;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public interface Expression {

    public QueryLexer.Operation getOperation();

    public List<Nupkg> execute();
    
}

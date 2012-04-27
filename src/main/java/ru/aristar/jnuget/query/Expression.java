package ru.aristar.jnuget.query;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public interface Expression {

    public List<Nupkg> execute();
}

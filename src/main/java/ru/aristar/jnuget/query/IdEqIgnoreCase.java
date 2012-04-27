package ru.aristar.jnuget.query;

import java.util.List;
import java.util.Queue;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class IdEqIgnoreCase implements Expression {

    public String value;

    public List<Nupkg> execute() {
        return null;
    }

    public static IdEqIgnoreCase parse(Queue<String> tokens) throws NugetFormatException {
        IdEqIgnoreCase expression = new IdEqIgnoreCase();
        QueryLexer.assertToken(tokens.poll(), "(");
        QueryLexer.assertToken(tokens.poll(), "Id");
        QueryLexer.assertToken(tokens.poll(), ")");
        QueryLexer.assertToken(tokens.poll(), "eq");
        QueryLexer.assertToken(tokens.poll(), "'");
        expression.value = tokens.poll();
        QueryLexer.assertToken(tokens.poll(), "'");
        return expression;
    }
}

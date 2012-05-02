package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.Queue;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class IdEqIgnoreCase implements Expression {

    public String packageId;

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        return packageSource.getPackages(packageId);
    }

 

    public static IdEqIgnoreCase parse(Queue<String> tokens) throws NugetFormatException {
        IdEqIgnoreCase expression = new IdEqIgnoreCase();
        QueryLexer.assertToken(tokens.poll(), "(");
        QueryLexer.assertToken(tokens.poll(), "Id");
        QueryLexer.assertToken(tokens.poll(), ")");
        QueryLexer.assertToken(tokens.poll(), "eq");
        QueryLexer.assertToken(tokens.poll(), "'");
        expression.packageId = tokens.poll();
        QueryLexer.assertToken(tokens.poll(), "'");
        return expression;
    }
}

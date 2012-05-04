package ru.aristar.jnuget.query;

import java.util.Collection;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Выражение сравнения идентификатора без учета регистра
 *
 * @author sviridov
 */
public class IdEqIgnoreCase implements Expression {

    /**
     * Идентификатор пакета
     */
    private String packageId;

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        return packageSource.getPackages(getPackageId());
    }

    /**
     * Создает выражение сравнения идентификатора без учета регистра
     *
     * @param tokens очередь токенов
     * @return выражение сравнения идентификатора без учета регистра
     * @throws NugetFormatException токены не соответствуют формату запроса
     * NuGet
     */
    public static IdEqIgnoreCase parse(java.util.Queue<java.lang.String> tokens) throws NugetFormatException {
        IdEqIgnoreCase expression = new IdEqIgnoreCase();
        QueryLexer.assertToken(tokens.poll(), "(");
        QueryLexer.assertToken(tokens.poll(), "Id");
        QueryLexer.assertToken(tokens.poll(), ")");
        QueryLexer.assertToken(tokens.poll(), "eq");
        QueryLexer.assertToken(tokens.poll(), "'");
        expression.setPackageId(tokens.poll());
        QueryLexer.assertToken(tokens.poll(), "'");
        return expression;
    }

    /**
     * @return Идентификатор пакета
     */
    public String getPackageId() {
        return packageId;
    }

    /**
     * @param packageId Идентификатор пакета
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
}

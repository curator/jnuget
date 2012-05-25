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
     * Сравнивает токен с эталоном
     *
     * @param actual токен
     * @param expected эталон
     * @throws NugetFormatException токен не соответствует эталону
     */
    private static void assertToken(String actual, String expected) throws NugetFormatException {
        if (!actual.equalsIgnoreCase(expected)) {
            throw new NugetFormatException("Встретился токен '" + actual
                    + "', когда ожидался '" + expected + "'");
        }
    }
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
        assertToken(tokens.poll(), "(");
        assertToken(tokens.poll(), "Id");
        assertToken(tokens.poll(), ")");
        assertToken(tokens.poll(), "eq");
        assertToken(tokens.poll(), "'");
        expression.setPackageId(tokens.poll());
        assertToken(tokens.poll(), "'");
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

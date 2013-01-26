package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashSet;
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
    public static void assertToken(String actual, String expected) throws NugetFormatException {
        if (!actual.equalsIgnoreCase(expected)) {
            throw new NugetFormatException("Встретился токен '" + actual
                    + "', когда ожидался '" + expected + "'");
        }
    }
    /**
     * Идентификатор пакета
     */
    private String packageId;

    /**
     * @param id идентификатор пакета
     */
    public IdEqIgnoreCase(String id) {
        packageId = id;
    }

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
        assertToken(tokens.poll(), "(");
        assertToken(tokens.poll(), "Id");
        assertToken(tokens.poll(), ")");
        assertToken(tokens.poll(), "eq");
        assertToken(tokens.poll(), "'");
        IdEqIgnoreCase expression = new IdEqIgnoreCase(tokens.poll());
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

    @Override
    public String toString() {
        return "tolower(Id) eq '" + packageId.toLowerCase() + "'";
    }

    @Override
    public Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages) {
        HashSet<Nupkg> result = new HashSet<>();
        for (Nupkg nupkg : packages) {
            if (nupkg.getId().equalsIgnoreCase(packageId)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    @Override
    public boolean hasFilterPriority() {
        return false;
    }
}

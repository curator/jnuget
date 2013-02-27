package ru.aristar.jnuget.query;

import java.util.ArrayList;
import java.util.Collection;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Выражение сравнения идентификатора без учета регистра
 *
 * @author sviridov
 */
public class IdNe extends AbstractExpression {

    /**
     * Идентификатор пакета
     */
    private String packageId;

    /**
     * @param id идентификатор пакета
     */
    public IdNe(String id) {
        if (id == null || id.isEmpty() || id.equalsIgnoreCase("null")) {
            packageId = null;
        } else {
            packageId = id;
        }
    }

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        ArrayList<Nupkg> result = new ArrayList<>();
        for (Nupkg nupkg : packageSource.getPackages()) {
            if (accept(nupkg)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    /**
     * Создает выражение сравнения идентификатора без учета регистра
     *
     * @param tokens очередь токенов
     * @return выражение сравнения идентификатора без учета регистра
     * @throws NugetFormatException токены не соответствуют формату запроса
     * NuGet
     */
    public static IdNe parse(java.util.Queue<java.lang.String> tokens) throws NugetFormatException {
        assertToken(tokens.poll(), "ne");
        IdNe expression = new IdNe(tokens.poll());
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
    public boolean hasFilterPriority() {
        return true;
    }

    @Override
    public boolean accept(Nupkg nupkg) {
        if (packageId == null) {
            return nupkg.getId() != null;
        }
        return !nupkg.getId().equalsIgnoreCase(packageId);
    }

    @Override
    public String toString() {
        return "Id ne " + packageId.toLowerCase();
    }
}

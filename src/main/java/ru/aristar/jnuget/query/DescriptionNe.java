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
public class DescriptionNe extends AbstractExpression {

    /**
     * Описание пакета
     */
    private String description;

    /**
     * @param description описание пакета
     */
    public DescriptionNe(String description) {
        if (description == null || description.isEmpty() || description.equalsIgnoreCase("null")) {
            this.description = null;
        } else {
            this.description = description;
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
    public static DescriptionNe parse(java.util.Queue<java.lang.String> tokens) throws NugetFormatException {
        assertToken(tokens.poll(), "ne");
        DescriptionNe expression = new DescriptionNe(tokens.poll());
        return expression;
    }

    /**
     * @return описание пакета
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description описание пакета
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean hasFilterPriority() {
        return true;
    }

    @Override
    public boolean accept(Nupkg nupkg) {
        try {
            if (description == null) {
                return nupkg.getNuspecFile().getDescription() != null;
            }
            return !nupkg.getNuspecFile().getDescription().equalsIgnoreCase(description);
        } catch (NugetFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Id ne " + description.toLowerCase();
    }
}

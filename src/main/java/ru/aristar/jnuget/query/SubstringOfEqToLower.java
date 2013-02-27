package ru.aristar.jnuget.query;

import java.util.ArrayList;
import java.util.Collection;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;
import static java.text.MessageFormat.format;

/**
 * Выражение сравнения идентификатора без учета регистра
 *
 * @author sviridov
 */
public class SubstringOfEqToLower extends AbstractExpression {

    /**
     * Идентификатор пакета
     */
    private String field;
    private final String value;

    /**
     * @param field поле в пакете пакете
     * @param value подстрока в поле
     */
    public SubstringOfEqToLower(String field, String value) {
        this.field = field;
        this.value = value;
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
    public static SubstringOfEqToLower parse(java.util.Queue<java.lang.String> tokens) throws NugetFormatException {
        assertToken(tokens.poll(), "(");
        assertToken(tokens.poll(), "'");
        String value = tokens.poll();
        assertToken(tokens.poll(), "'");
        assertToken(tokens.poll(), "tolower");
        assertToken(tokens.poll(), "(");
        String field = tokens.poll();
        assertToken(tokens.poll(), ")");
        assertToken(tokens.poll(), ")");
        assertToken(tokens.poll(), ")");
        SubstringOfEqToLower expression = new SubstringOfEqToLower(field, value);
        return expression;
    }

    /**
     * @return Идентификатор пакета
     */
    public String getPackageId() {
        return field;
    }

    /**
     * @param packageId Идентификатор пакета
     */
    public void setPackageId(String packageId) {
        this.field = packageId;
    }

    @Override
    public boolean hasFilterPriority() {
        return true;
    }

    @Override
    public boolean accept(Nupkg nupkg) {
        try {
            switch (field.toLowerCase()) {
                case "id": {
                    return nupkg.getId().toLowerCase().contains(value.toLowerCase());
                }
                case "description": {
                    return nupkg.getNuspecFile().getDescription().toLowerCase().contains(value.toLowerCase());
                }
                case "tags": {
                    return true; //TODO Доделать
                }
                default:
                    throw new NugetFormatException(format("Поле \"{0}\" не поддерживается.", field));
            }
        } catch (NugetFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "tolower(Id) eq '" + field.toLowerCase() + "'";
    }
}

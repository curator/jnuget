package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashSet;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public abstract class AbstractExpression implements Expression {

    /**
     * Сравнивает токен с эталоном
     *
     * @param actual токен
     * @param expected эталон
     * @throws NugetFormatException токен не соответствует эталону
     */
    public static void assertToken(String actual, String expected) throws NugetFormatException {
        if (!actual.equalsIgnoreCase(expected)) {
            throw new NugetFormatException("Встретился токен '" + actual + "', когда ожидался '" + expected + "'");
        }
    }

    @Override
    public Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages) {
        HashSet<Nupkg> result = new HashSet<>();
        for (Nupkg nupkg : packages) {
            if (accept(nupkg)) {
                result.add(nupkg);
            }
        }
        return result;
    }
}

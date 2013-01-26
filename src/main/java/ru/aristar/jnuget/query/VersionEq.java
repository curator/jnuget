package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import static ru.aristar.jnuget.query.IdEqIgnoreCase.assertToken;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Выражение сравнения версии
 *
 * @author sviridov
 */
public class VersionEq implements Expression {

    /**
     * Распознает строку с выражением
     *
     * @param tokens токены строки выражения
     * @return распознанное выражение
     * @throws NugetFormatException некорректная строка выражения
     */
    static VersionEq parse(Queue<String> tokens) throws NugetFormatException {
        assertToken(tokens.poll(), "eq");
        assertToken(tokens.poll(), "'");
        Version version = Version.parse(tokens.poll());
        assertToken(tokens.poll(), "'");
        return new VersionEq(version);
    }
    /**
     * Версия
     */
    private final Version version;

    /**
     * @param version версия пакета
     */
    public VersionEq(Version version) {
        this.version = version;
    }

    /**
     * @return версия пакета
     */
    public Version getVersion() {
        return version;
    }

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        //TODO Продумать ограничения по идентификаторам
        HashSet<Nupkg> result = new HashSet<>();
        for (Nupkg nupkg : packageSource.getPackages()) {
            if (nupkg.getVersion().equals(version)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Version eq '" + version + "'";
    }

    @Override
    public Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages) {
        HashSet<Nupkg> result = new HashSet<>();
        for (Nupkg nupkg : packages) {
            if (version.equals(nupkg.getVersion())) {
                result.add(nupkg);
            }
        }
        return result;
    }

    @Override
    public boolean hasFilterPriority() {
        return true;
    }
}

package ru.aristar.jnuget.query;

import java.util.Collection;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Выражение,использующееся для поиска пакетов
 *
 * @author sviridov
 */
public interface Expression {

    /**
     * Выполнить выражение для источника пакетов
     *
     * @param packageSource источник пакетов
     * @return список пакетов, соответствующих выражению
     */
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource);
}

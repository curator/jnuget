package ru.aristar.jnuget.query;

import java.util.Collection;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Возвращает последние версии пакетов из хранилища
 *
 * @author sviridov
 */
public class LatestVersionExpression implements Expression {

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        return packageSource.getLastVersionPackages();
    }
}

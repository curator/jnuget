package ru.aristar.jnuget.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

    @Override
    public Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages) {
        HashMap<String, Nupkg> result = new HashMap<>();
        for (Nupkg nupkg : packages) {
            Nupkg exisingPackage = result.get(nupkg.getId());
            if (exisingPackage == null || nupkg.getVersion().compareTo(exisingPackage.getVersion()) > 0) {
                result.put(nupkg.getId(), nupkg);
            }
        }
        return result.values();
    }

    @Override
    public boolean hasFilterPriority() {
        return false;
    }
}

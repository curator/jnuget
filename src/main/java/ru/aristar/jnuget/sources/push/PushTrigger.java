package ru.aristar.jnuget.sources.push;

import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public interface PushTrigger {

    public void doAction(Nupkg nupkg, PackageSource<? extends Nupkg> packageSource) throws NugetPushException;
}

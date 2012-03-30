package ru.aristar.jnuget.sources.push;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class RemoveOldVersionTrigger implements PushTrigger {

    private int maxPackageCount = 10;

    private int getMaxPackageCount() {
        return maxPackageCount;
    }

    public void setMaxPackageCount(int maxPackageCount) {
        this.maxPackageCount = maxPackageCount;
    }

    private static class NupkgVersionComparator implements Comparator<Nupkg> {

        @Override
        public int compare(Nupkg o1, Nupkg o2) {
            return o1.getVersion().compareTo(o2.getVersion());
        }
    }

    private List<? extends Nupkg> toSortedList(Collection<? extends Nupkg> nupkgs) {
        List<? extends Nupkg> result;
        if (nupkgs instanceof List) {
            result = (List<? extends Nupkg>) nupkgs;
        } else {
            result = new ArrayList<>(nupkgs);
        }
        Collections.sort(result, new NupkgVersionComparator());
        return result;
    }

    @Override
    public void doAction(Nupkg nupkg, PackageSource<? extends Nupkg> packageSource) throws NugetPushException {
        Collection<? extends Nupkg> nupkgs = packageSource.getPackages(nupkg.getId());
        List<? extends Nupkg> sortedNupkgs = toSortedList(nupkgs);
        while (sortedNupkgs.size() > getMaxPackageCount()) {
            Nupkg pkg = sortedNupkgs.get(sortedNupkgs.size() - 1);
            packageSource.removePackage(pkg.getId(), pkg.getVersion());
        }
    }
}

package ru.aristar.jnuget.sources.push;

import java.util.*;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.ui.descriptors.Property;

/**
 *
 * @author sviridov
 */
public class RemoveOldVersionTrigger implements AfterTrigger {

    /**
     * Максимально допустимое число пакетов с одинаковым идентификатором в
     * хранилище по умолчанию.
     */
    public static final int DEFAULT_MAX_PACKAGE_COUNT = 10;
    /**
     * Максимально допустимое число пакетов с одинаковым идентификатором в
     * хранилище.
     */
    private int maxPackageCount = DEFAULT_MAX_PACKAGE_COUNT;

    /**
     * @see DEFAULT_MAX_PACKAGE_COUNT
     * @return Максимально допустимое число пакетов с одинаковым идентификатором
     * в хранилище.
     */
    @Property
    public int getMaxPackageCount() {
        return maxPackageCount;
    }

    /**
     * @see DEFAULT_MAX_PACKAGE_COUNT
     * @param maxPackageCount Максимально допустимое число пакетов с одинаковым
     * идентификатором в хранилище.
     */
    public void setMaxPackageCount(int maxPackageCount) {
        this.maxPackageCount = maxPackageCount;
    }

    /**
     * Компаратор для реверсивного (младший сверху) сравнения пакетов по их
     * версиям
     */
    private static class NupkgReverseVersionComparator implements Comparator<Nupkg> {

        @Override
        public int compare(Nupkg o1, Nupkg o2) {
            return o2.getVersion().compareTo(o1.getVersion());
        }
    }

    /**
     * Преобразует коллекцию пакетов в обратно сортированный по версиям список
     *
     * @param nupkgs коллекция пакетов
     * @return обратно сортированный по версиям пакетов список
     */
    private List<? extends Nupkg> toSortedList(Collection<? extends Nupkg> nupkgs) {
        List<? extends Nupkg> result;
        if (nupkgs instanceof List) {
            result = (List<? extends Nupkg>) nupkgs;
        } else {
            result = new ArrayList<>(nupkgs);
        }
        Collections.sort(result, new NupkgReverseVersionComparator());
        return result;
    }

    @Override
    public void doAction(Nupkg nupkg, PackageSource<? extends Nupkg> packageSource) throws NugetPushException {
        Collection<? extends Nupkg> nupkgs = packageSource.getPackages(nupkg.getId());
        List<? extends Nupkg> sortedNupkgs = toSortedList(nupkgs);
        while (sortedNupkgs.size() > getMaxPackageCount()) {
            Nupkg pkg = sortedNupkgs.get(sortedNupkgs.size() - 1);
            packageSource.removePackage(pkg);
            sortedNupkgs = sortedNupkgs.subList(0, sortedNupkgs.size() - 1);
        }
    }
}

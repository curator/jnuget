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
    Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource);

    /**
     * Отфильтровать пакеты, удовлетворяющие выражению
     *
     * @param packages исходная коллекция пакетов
     * @return отфильтрованные пакеты
     */
    Collection<? extends Nupkg> filter(Collection<? extends Nupkg> packages);

    /**
     * Болле приоритетным является выполнение в режиме фильтра
     *
     * @return true/false
     */
    boolean hasFilterPriority();

    /**
     * Пакет проходит выражение
     *
     * @param nupkg пакет
     * @return проходит или нет
     */
    boolean accept(Nupkg nupkg);
}

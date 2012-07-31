package ru.aristar.jnuget.sources.push;

import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Триггер, запускающийся до добавления пакета в хранилище
 *
 * @author sviridov
 */
public interface BeforeTrigger {

    /**
     * Действие, которое должно быть выполнено при срабатывании триггера
     *
     * @param nupkg пакет, вставка которого вызвала срабатывание триггера
     * @param packageSource хранилище, в которое производилась вставка триггера
     * @return продолжать или нет помещение пакета в хранилище
     * @throws NugetPushException ошибка, произошедшая при срабатывании триггера
     */
    public boolean doAction(Nupkg nupkg, PackageSource<? extends Nupkg> packageSource) throws NugetPushException;
}

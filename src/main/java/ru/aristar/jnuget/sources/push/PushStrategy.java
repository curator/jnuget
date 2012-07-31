package ru.aristar.jnuget.sources.push;

import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public interface PushStrategy {

    /**
     * Проверяет - разрешено ли помещать пакет в хранилище
     *
     * @param nupkgFile пакет NuGet
     * @return true, если разрешено поместить пакет в хранилище
     */
    boolean canPush(Nupkg nupkgFile);

    /**
     * @return триггеры, выполняющиеся до помещения пакета в хранилище
     */
    List<BeforeTrigger> getBeforeTriggers();

    /**
     * @return триггеры, выполняющиеся после помещения пакета в хранилище
     */
    List<AfterTrigger> getAftherTriggers();
}

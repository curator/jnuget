package ru.aristar.jnuget.sources;

import ru.aristar.jnuget.files.ClassicNupkg;
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
     * @param apiKey ключ (пароль)
     * @return true, если разрешено поместить пакет в хранилище
     */
    boolean canPush(Nupkg nupkgFile, String apiKey);
}
package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.NugetPushException;
import ru.aristar.jnuget.sources.push.PushStrategy;
import ru.aristar.jnuget.sources.push.PushTrigger;
import ru.aristar.jnuget.sources.push.SimplePushStrategy;

/**
 * Абстрактное хранилище пакетов
 *
 * @param <T> тип пакета в хранилище
 * @author sviridov
 */
public abstract class AbstractPackageSource<T extends Nupkg> implements PackageSource<T> {

    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Стратегия помещения пакета в хранилище
     */
    protected PushStrategy strategy;

    @Override
    public boolean pushPackage(Nupkg nupkgFile, String apiKey) throws IOException {
        if (!getPushStrategy().canPush(nupkgFile, apiKey)) {
            return false;
        }
        try {
            for (PushTrigger pushTrigger : getPushStrategy().getBeforeTriggers()) {
                pushTrigger.doAction(nupkgFile, this);
            }
        } catch (NugetPushException e) {
            logger.error("Ошибка при обработке afther триггеров", e);
            return false;
        }
        pushPackage(nupkgFile);
        try {
            for (PushTrigger pushTrigger : getPushStrategy().getAftherTriggers()) {
                pushTrigger.doAction(nupkgFile, this);
            }
        } catch (NugetPushException e) {
            logger.error("Ошибка при обработке before триггеров", e);
            return false;
        }
        return true;
    }

    /**
     * Помещает пакет в хранилище
     *
     * @param nupkg пакет
     * @throws IOException ошибка записи
     */
    protected abstract void pushPackage(Nupkg nupkg) throws IOException;

    @Override
    public PushStrategy getPushStrategy() {
        if (strategy == null) {
            strategy = new SimplePushStrategy(false);
        }
        return strategy;
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Извлекает информацию о последних версиях всех пакетов
     *
     * @param <K> Тип пакета NuGet
     * @param list общий список всех пакетов
     * @param ignoreCase нужно ли игнорировать регистр символов
     * @return список последних версий пакетов
     */
    public static <K extends Nupkg> Collection<K> extractLastVersion(
            Collection<K> list, boolean ignoreCase) {
        Map<String, K> map = new HashMap<>();
        for (K pack : list) {
            String packageId = ignoreCase ? pack.getId().toLowerCase() : pack.getId();
            // Указанный пакет еще учитывался
            if (!map.containsKey(packageId)) {
                map.put(packageId, pack);
            } else { // Пакет уже попадался, сравниваем версии
                Version saved = map.get(packageId).getVersion();
                // Версия пакета новее, чем сохраненная
                if (saved.compareTo(pack.getVersion()) < 0) {
                    map.put(packageId, pack);
                }
            }
        }
        return map.values();
    }
}

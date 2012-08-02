package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.AfterTrigger;
import ru.aristar.jnuget.sources.push.BeforeTrigger;
import ru.aristar.jnuget.sources.push.NugetPushException;
import ru.aristar.jnuget.sources.push.PushStrategy;

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
    public boolean pushPackage(Nupkg nupkgFile) throws IOException {
        if (!getPushStrategy().canPush(nupkgFile)) {
            return false;
        }
        try {
            //TODO перенести метод в стратегию
            for (BeforeTrigger pushTrigger : getPushStrategy().getBeforeTriggers()) {
                if (!pushTrigger.doAction(nupkgFile, this)) {
                    return false;
                }
            }
            processPushPackage(nupkgFile);
            for (AfterTrigger trigger : getPushStrategy().getAftherTriggers()) {
                trigger.doAction(nupkgFile, this);
            }
        } catch (NugetPushException e) {
            logger.error("Ошибка помещения пакета в хранилище", e);
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
    protected abstract void processPushPackage(Nupkg nupkg) throws IOException;

    @Override
    public PushStrategy getPushStrategy() {
        if (strategy == null) {
            strategy = new PushStrategy(false);
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
     * @return список последних версий пакетов
     */
    public static <K extends Nupkg> Collection<K> extractLastVersion(
            Collection<K> list) {
        Map<String, K> map = new HashMap<>();
        for (K pack : list) {
            String packageId = pack.getId().toLowerCase();
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

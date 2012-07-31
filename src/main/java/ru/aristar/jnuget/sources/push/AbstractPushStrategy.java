package ru.aristar.jnuget.sources.push;

import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный класс стратегии фиксации
 *
 * @author sviridov
 */
abstract public class AbstractPushStrategy implements PushStrategy {

    /**
     * Триггеры, выполняющиеся до помещения пакета в хранилище
     */
    private List<BeforeTrigger> beforeTriggers;
    /**
     * Триггеры, выполняющиеся после помещения пакета в хранилище
     */
    private List<AfterTrigger> aftherTriggers;

    @Override
    public List<AfterTrigger> getAftherTriggers() {
        if (aftherTriggers == null) {
            aftherTriggers = new ArrayList<>();
        }
        return aftherTriggers;
    }

    @Override
    public List<BeforeTrigger> getBeforeTriggers() {
        if (beforeTriggers == null) {
            beforeTriggers = new ArrayList<>();
        }
        return beforeTriggers;
    }
}

package ru.aristar.jnuget.sources.push;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс стратегии модификации пакета в хранилище
 *
 * @author sviridov
 */
public class ModifyStrategy {

    /**
     * Триггеры, выполняющиеся до помещения пакета в хранилище
     */
    private List<BeforeTrigger> beforePushTriggers;
    /**
     * Триггеры, выполняющиеся после помещения пакета в хранилище
     */
    private List<AfterTrigger> aftherPushTriggers;
    //TODO Добавить BeforeDelete и AfterDelete триггеры
    /**
     * Флаг: разрешена или нет публикация
     */
    protected boolean canPush;
    /**
     * Разрешено или нет удаление
     */
    protected boolean canDelete;

    /**
     * Конструктор по умолчанию
     */
    public ModifyStrategy() {
        this(false, false);
    }

    /**
     * @param canPush разрешена или нет публикация
     */
    public ModifyStrategy(boolean canPush) {
        this(canPush, false);
    }

    /**
     * @param canPush разрешена или нет публикация
     * @param canDelete разрешено или нет удаление
     */
    public ModifyStrategy(boolean canPush, boolean canDelete) {
        this.canPush = canPush;
        this.canDelete = canDelete;
    }

    /**
     * @return триггеры, выполняющиеся после помещения пакета в хранилище
     */
    public List<AfterTrigger> getAftherPushTriggers() {
        if (aftherPushTriggers == null) {
            aftherPushTriggers = new ArrayList<>();
        }
        return aftherPushTriggers;
    }

    /**
     * @return триггеры, выполняющиеся до помещения пакета в хранилище
     */
    public List<BeforeTrigger> getBeforePushTriggers() {
        if (beforePushTriggers == null) {
            beforePushTriggers = new ArrayList<>();
        }
        return beforePushTriggers;
    }

    /**
     * @return разрешено или нет удаление
     */
    public boolean canDelete() {
        return canDelete;
    }
    //TODO Добавить проверку на разрешение удаления

    /**
     * @param canDelete разрешено или нет удаление
     */
    public void setСanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    /**
     * @return разрешена или нет публикация
     */
    public boolean canPush() {
        return canPush;
    }

    /**
     * @param canPush разрешена или нет публикация
     */
    public void setCanPush(boolean canPush) {
        this.canPush = canPush;
    }
}

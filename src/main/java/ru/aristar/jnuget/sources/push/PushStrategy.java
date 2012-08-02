package ru.aristar.jnuget.sources.push;

import java.util.ArrayList;
import java.util.List;
import ru.aristar.jnuget.files.Nupkg;

/**
 * Абстрактный класс стратегии фиксации
 *
 * @author sviridov
 */
public class PushStrategy {

    /**
     * Триггеры, выполняющиеся до помещения пакета в хранилище
     */
    private List<BeforeTrigger> beforeTriggers;
    /**
     * Триггеры, выполняющиеся после помещения пакета в хранилище
     */
    private List<AfterTrigger> aftherTriggers;
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
    public PushStrategy() {
    }

    /**
     * @param canPush разрешена или нет публикация
     */
    public PushStrategy(boolean allow) {
        this.canPush = allow;
    }

    public List<AfterTrigger> getAftherTriggers() {
        if (aftherTriggers == null) {
            aftherTriggers = new ArrayList<>();
        }
        return aftherTriggers;
    }

    public List<BeforeTrigger> getBeforeTriggers() {
        if (beforeTriggers == null) {
            beforeTriggers = new ArrayList<>();
        }
        return beforeTriggers;
    }

    public boolean canDelete(Nupkg nupkg) {
        return canDelete;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public boolean canPush(Nupkg nupkgFile) {
        return canPush;
    }

    public boolean canPush() {
        return canPush;
    }

    public void setAllowDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    /**
     * @param canPush разрешена или нет публикация
     */
    public void setAllowPush(boolean allow) {
        this.canPush = allow;
    }
}

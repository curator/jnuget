package ru.aristar.jnuget.sources.push;

import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.ui.descriptors.Property;

/**
 * Простая стратегия публикации с флагом разрешить/запретить
 *
 * @author sviridov
 */
public class SimplePushStrategy extends AbstractPushStrategy implements PushStrategy {

    /**
     * Флаг: разрешена или нет публикация
     */
    private boolean allow;

    /**
     * @return разрешена или нет публикация
     */
    @Property()
    public boolean isAllow() {
        return allow;
    }

    /**
     * @param allow разрешена или нет публикация
     */
    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    /**
     * Конструктор по умолчанию
     */
    public SimplePushStrategy() {
    }

    /**
     * @param allow разрешена или нет публикация
     */
    public SimplePushStrategy(boolean allow) {
        this.allow = allow;
    }

    @Override
    public boolean canPush(Nupkg nupkgFile, String apiKey) {
        return isAllow();
    }
}

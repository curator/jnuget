package ru.aristar.jnuget.sources;

import ru.aristar.jnuget.files.NupkgFile;

/**
 * Простая стратегия публикации с флагом разрешить/запретить
 *
 * @author sviridov
 */
public class SimplePushStrategy implements PushStrategy {

    /**
     * Флаг: разрешена или нет публикация
     */
    private boolean allow;

    /**
     * @return разрешена или нет публикация
     */
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
    public boolean canPush(NupkgFile nupkgFile, String apiKey) {
        return isAllow();
    }
}

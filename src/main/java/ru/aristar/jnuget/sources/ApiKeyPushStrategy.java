package ru.aristar.jnuget.sources;

import java.util.Objects;
import ru.aristar.jnuget.files.ClassicNupkg;

/**
 * Стратегия публикации пакетов, основанная на проверке ключа
 *
 * @author sviridov
 */
public class ApiKeyPushStrategy implements PushStrategy {

    /**
     * Ключ (пароль), разрешающий публикацию пакета
     */
    private String apiKey;

    /**
     * Конструктор, задающий значение ключа
     *
     * @param apiKey ключ (пароль)
     */
    public ApiKeyPushStrategy(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Конструктор по умолчанию
     */
    public ApiKeyPushStrategy() {
    }


    @Override
    public boolean canPush(ClassicNupkg nupkgFile, String apiKey) {
        return Objects.equals(this.apiKey, apiKey);
    }
}

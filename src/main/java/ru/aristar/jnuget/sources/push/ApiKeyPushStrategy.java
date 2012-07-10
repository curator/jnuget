package ru.aristar.jnuget.sources.push;

import java.util.Objects;
import ru.aristar.jnuget.files.Nupkg;

/**
 * Стратегия публикации пакетов, основанная на проверке ключа
 *
 * @author sviridov
 */
public class ApiKeyPushStrategy extends AbstractPushStrategy implements PushStrategy {

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
    public boolean canPush(Nupkg nupkgFile, String apiKey) {
        return Objects.equals(this.apiKey, apiKey);
    }

    /**
     * @return Ключ (пароль), разрешающий публикацию пакета
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey Ключ (пароль), разрешающий публикацию пакета
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

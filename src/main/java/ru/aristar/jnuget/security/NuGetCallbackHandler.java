package ru.aristar.jnuget.security;

import java.io.IOException;
import javax.security.auth.callback.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.sources.push.NugetPushException;

/**
 *
 * @author sviridov
 */
public class NuGetCallbackHandler implements CallbackHandler {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Ключ доступа
     */
    private final String apiKey;
    /**
     * Имя пользователя
     */
    private final String userName;
    /**
     * Пароль пользователя
     */
    private final char[] password;

    /**
     * @param apiKey ключ доступа
     */
    public NuGetCallbackHandler(String apiKey) {
        this.apiKey = apiKey;
        this.userName = null;
        this.password = null;
    }

    /**
     * @param login имя пользователя
     * @param password пароль пользователя
     */
    public NuGetCallbackHandler(String login, String password) {
        this.userName = login;
        this.password = password.toCharArray();
        this.apiKey = null;
    }

    /**
     * Производит поиск объекта обратного вызова по его классу
     *
     * @param <T> тип
     * @param callbacks массив объектов обратного вызова
     * @param c класс искомого объекта
     * @return искомый объект
     * @throws NugetPushException объект обратного вызова не найден
     */
    private <T extends Callback> T findCallback(Callback[] callbacks, Class<T> c) throws NugetPushException {
        for (Callback callback : callbacks) {
            if (callback.getClass() == c) {
                return (T) callback;
            }
        }
        throw new NugetPushException("Объект обратного вызова " + c + " не найден");
    }
    
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        try {
            if (apiKey != null) {
                findCallback(callbacks, NameCallback.class).setName(userName);
                findCallback(callbacks, PasswordCallback.class).setPassword(password);
            } else {
                findCallback(callbacks, ApiKeyCallback.class).setApiKey(apiKey);
            }
        } catch (NugetPushException e) {
            logger.warn("ошибка авторизации", e);
        }
    }
}

package ru.aristar.jnuget.security;

import java.io.IOException;
import javax.security.auth.callback.*;
import ru.aristar.jnuget.sources.push.NugetPushException;

/**
 *
 * @author sviridov
 */
public class NuGetCallbackHandler implements CallbackHandler {

    private final String apiKey;
    private final String userName;
    private final char[] password;

    public NuGetCallbackHandler(String apiKey) {
        this.apiKey = apiKey;
        this.userName = null;
        this.password = null;
    }

    public NuGetCallbackHandler(String login, String password) {
        this.userName = login;
        this.password = password.toCharArray();
        this.apiKey = null;
    }

    private <T extends Callback> T findCallback(Callback[] callbacks, Class<T> c) throws NugetPushException {
        for (Callback callback : callbacks) {
            if (callback.getClass() == c) {
                return (T) callback;
            }
        }
        throw new NugetPushException("Класс обратного вызова " + c + " не найден");
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
            //TODO обработать логирование
        }
    }
}

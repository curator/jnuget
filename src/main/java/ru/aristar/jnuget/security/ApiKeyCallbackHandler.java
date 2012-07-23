package ru.aristar.jnuget.security;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 *
 * @author sviridov
 */
public class ApiKeyCallbackHandler implements CallbackHandler {

    private final String apiKey;

    public ApiKeyCallbackHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof ApiKeyCallback) {
                ApiKeyCallback apiKeyCallback = (ApiKeyCallback) callback;
                apiKeyCallback.setApiKey(apiKey);
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}

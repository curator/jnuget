package ru.aristar.jnuget.security;

import javax.security.auth.callback.Callback;

/**
 *
 * @author sviridov
 */
public class ApiKeyCallback implements Callback {

    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

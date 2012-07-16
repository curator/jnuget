package ru.aristar.jnuget.security;

import javax.security.auth.callback.Callback;

/**
 * Класс для указания ключа доступа при авторизации
 *
 * @author sviridov
 */
public class ApiKeyCallback implements Callback {

    /**
     * Ключ доступа
     */
    private String apiKey;

    /**
     * @return ключ доступа
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey ключ доступа
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

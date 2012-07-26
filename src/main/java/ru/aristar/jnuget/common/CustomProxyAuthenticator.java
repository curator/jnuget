package ru.aristar.jnuget.common;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Класс авторизации на прокси сервере
 *
 * @author sviridov
 */
public class CustomProxyAuthenticator extends Authenticator {

    /**
     * Логин
     */
    private String login;
    /**
     * Пароль
     */
    private String password;

    /**
     * @param login логин
     * @param password пароль
     */
    public CustomProxyAuthenticator(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(login, password.toCharArray());
    }
}

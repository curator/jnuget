package ru.aristar.jnuget.security;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * Реализует авторизацию по ключу
 *
 * @author sviridov
 */
public class XmlFileApiKeyLoginModule extends XmlFileAbstractLoginModule implements LoginModule {

    @Override
    public boolean login() throws LoginException {
        try {
            ApiKeyCallback apiKeyCallback = new ApiKeyCallback();
            callbackHandler.handle(new Callback[]{apiKeyCallback});
            logger.info("Запрос авторизации: " + apiKeyCallback.getApiKey());
            user = usersOptions.findUser(apiKeyCallback.getApiKey());
            if (user == null) {
                return false;
            } else {
                return true;
            }
        } catch (IOException | UnsupportedCallbackException e) {
            logger.error("Ошибка в процессе авторизации", e);
            return false;
        }
    }
}

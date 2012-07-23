package ru.aristar.jnuget.security;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * Реализует авторизацию по логину и паролю
 *
 * @author sviridov
 */
public class XmlFilePasswordLoginModule extends XmlFileAbstractLoginModule implements LoginModule {

    @Override
    public boolean login() throws LoginException {
        try {
            NameCallback nameCallback = new NameCallback("login");
            PasswordCallback passwordCallback = new PasswordCallback("password", true);
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
            logger.info("Запрос авторизации: " + nameCallback.getName() + ":" + new String(passwordCallback.getPassword()));
            user = usersOptions.findUser(nameCallback.getName(), passwordCallback.getPassword());
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

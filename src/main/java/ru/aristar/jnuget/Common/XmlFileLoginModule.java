package ru.aristar.jnuget.Common;

import java.io.IOException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.security.User;
import ru.aristar.jnuget.security.UsersOptions;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
public class XmlFileLoginModule implements LoginModule {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Результат авотризации
     */
    private Subject subject;
    /**
     * Обработчик обратного вызова
     */
    private CallbackHandler callbackHandler;
    /**
     * Настройки пользователя
     */
    private User user;
    /**
     * Настройки всех пользователей
     */
    private UsersOptions usersOptions;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.usersOptions = PackageSourceFactory.getInstance().getOptions().getUserOptions();
        this.callbackHandler = callbackHandler;
        this.subject = subject;
    }

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

    @Override
    public boolean commit() throws LoginException {
        if (user == null) {
            return false;
        }
        subject.getPrincipals().add(new UserPrincipal(user.getName()));
        for (String role : user.getRoles()) {
            subject.getPrincipals().add(new RolePrincipal(role));
        }
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (subject != null) {
            subject.getPrincipals().clear();
        }
        subject = null;
        user = null;
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().clear();
        subject = null;
        user = null;
        return true;
    }
}

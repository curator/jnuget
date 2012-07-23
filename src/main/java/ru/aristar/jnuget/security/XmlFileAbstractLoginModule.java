package ru.aristar.jnuget.security;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Реализует авторизацию по настройкам в XML файле
 *
 * @author sviridov
 */
public abstract class XmlFileAbstractLoginModule implements LoginModule {

    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Результат авотризации
     */
    protected Subject subject;
    /**
     * Обработчик обратного вызова
     */
    protected CallbackHandler callbackHandler;
    /**
     * Настройки пользователя
     */
    protected User user;
    /**
     * Настройки всех пользователей
     */
    protected UsersOptions usersOptions;

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
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.usersOptions = PackageSourceFactory.getInstance().getOptions().getUserOptions();
        this.callbackHandler = callbackHandler;
        this.subject = subject;
    }

    @Override
    public abstract boolean login() throws LoginException;

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().clear();
        subject = null;
        user = null;
        return true;
    }
}

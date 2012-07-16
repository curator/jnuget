package ru.aristar.jnuget.Common;

import java.io.IOException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.security.ApiKeyCallback;

/**
 *
 * @author sviridov
 */
public class XmlFileLoginModule implements LoginModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Subject subject;
    private CallbackHandler callbackHandler;
    private String userName;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.callbackHandler = callbackHandler;
        this.subject = subject;
    }

    @Override
    public boolean login() throws LoginException {
        try {
            NameCallback nameCallback = new NameCallback("login");
            PasswordCallback passwordCallback = new PasswordCallback("password", true);
            ApiKeyCallback apiKeyCallback = new ApiKeyCallback();
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback, apiKeyCallback});
            logger.info("Запрос авторизации: " + nameCallback.getName() + ":" + new String(passwordCallback.getPassword()));
            userName = nameCallback.getName();
        } catch (IOException | UnsupportedCallbackException e) {
            logger.error("Ошибка в процессе авторизации", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (userName.equals("user")) {
            subject.getPrincipals().add(new RolePrincipal("GuiUser"));
        } else if (userName.equals("admin")) {
            subject.getPrincipals().add(new RolePrincipal("Administrator"));
        }
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().clear();
        return true;
    }
}

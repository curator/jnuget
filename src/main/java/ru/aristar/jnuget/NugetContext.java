package ru.aristar.jnuget;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.rss.NuPkgToRssTransformer;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.security.ApiKeyCallbackHandler;
import ru.aristar.jnuget.security.RolePrincipal;
import ru.aristar.jnuget.security.Role;

/**
 *
 * @author sviridov
 */
public class NugetContext {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Коневой URI приложения
     */
    private final URI rootUri;
    /**
     * Праметры авторизации пользователя
     */
    protected Subject subject;
    /**
     * Контекст авторизации пользователя
     */
    private LoginContext loginContext;

    /**
     * @param rootUri коневой URI приложения
     */
    public NugetContext(URI rootUri) {
        this.rootUri = rootUri;
        logger.debug("Создан контекст для приложения {}", new Object[]{rootUri});
    }

    /**
     * @return коневой URI приложения
     */
    public URI getRootUri() {
        return rootUri;
    }

    /**
     * Создает RSS вложение с информацией о пакете (дополняет его информацией от
     * сервера)
     *
     * @param nupkgFile пакет
     * @return RSS вложение
     * @throws NoSuchAlgorithmException ошибка вычисления HASH
     * @throws IOException ошибка чтения пакета
     * @throws NugetFormatException некорректная спецификация пакета
     */
    public PackageEntry createPackageEntry(Nupkg nupkgFile) throws NoSuchAlgorithmException, IOException, NugetFormatException {
        return new ContextPackageEntry(nupkgFile);
    }

    /**
     * Создает преобразователь пакетов в RSS ленту
     *
     * @return преобразователь пакетов в RSS ленту
     */
    public NuPkgToRssTransformer createToRssTransformer() {
        return new ContextNuPkgToRssTransformer();
    }

    /**
     * Авторизоваться в системе
     *
     * @param apiKey ключ авторизации
     * @throws LoginException ошибка авторизации
     */
    public void login(String apiKey) throws LoginException {
        this.loginContext = new LoginContext("ApikeyXmlAuth", new ApiKeyCallbackHandler(apiKey));
        loginContext.login();
        this.subject = loginContext.getSubject();
    }

    /**
     * Выйти из системы
     */
    public void logout() {
        try {
            loginContext.logout();
        } catch (LoginException ex) {
            logger.warn("Ошибка при выходе пользователя из системы", ex);
        }
    }

    /**
     * Обладает ли пользователь указанной ролью
     *
     * @param role роль
     * @return true, если у пользователя есть указанная роль
     */
    public boolean isUserInRole(Role role) {
        for (RolePrincipal rolePrincipal : subject.getPrincipals(RolePrincipal.class)) {
            if (rolePrincipal.getRole() == role) {
                return true;
            }
        }
        return false;
    }

    /**
     * Преобразователь в RSS, содержащий контекст
     */
    private class ContextNuPkgToRssTransformer extends NuPkgToRssTransformer {

        @Override
        protected NugetContext getContext() {
            return NugetContext.this;
        }
    }

    /**
     * RSS вложение, содержащее контекст
     */
    private class ContextPackageEntry extends PackageEntry {

        /**
         * @param nupkgFile пакет на основе кторого создается RSS вложение
         * @throws NoSuchAlgorithmException не указан алгоритм вычисления HASH
         * @throws IOException ошибка чтения пакета
         * @throws NugetFormatException некорректная спецификация пакета
         */
        public ContextPackageEntry(Nupkg nupkgFile) throws NoSuchAlgorithmException, IOException, NugetFormatException {
            super(nupkgFile);
        }

        @Override
        protected String getRootUri() {
            return NugetContext.this.rootUri.toString();
        }
    }
}

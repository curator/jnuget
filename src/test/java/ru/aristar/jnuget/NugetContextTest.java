package ru.aristar.jnuget;

import java.net.URI;
import java.net.URISyntaxException;
import javax.security.auth.Subject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ru.aristar.jnuget.security.Role;
import ru.aristar.jnuget.security.RolePrincipal;

/**
 * @author sviridov тесты контекста NuGet
 */
public class NugetContextTest {

    /**
     * Проверка того, находится ли пользователь в роли
     *
     * @throws URISyntaxException ошибка синтаксиса тестового URI
     */
    @Test
    public void testIsUserInRole() throws URISyntaxException {
        //GIVEN
        URI uri = new URI("http", "localhost", "/nuget", null);
        TestNugetContext nugetContext = new TestNugetContext(uri);
        Subject subject = new Subject();
        subject.getPrincipals().add(new RolePrincipal(Role.Administrator));
        nugetContext.setSubject(subject);
        //THEN
        assertThat(nugetContext.isUserInRole(Role.Administrator), is(equalTo(true)));
        assertThat(nugetContext.isUserInRole(Role.Push), is(equalTo(true)));
        assertThat(nugetContext.isUserInRole(Role.GuiUser), is(equalTo(true)));
    }

    /**
     * Класс контекста с открытой установкой пользователя
     */
    private class TestNugetContext extends NugetContext {

        /**
         * @param rootUri коневой URI приложения
         */
        public TestNugetContext(URI rootUri) {
            super(rootUri);
        }

        /**
         * @param subject праметры авторизации пользователя
         */
        public void setSubject(Subject subject) {
            this.subject = subject;
        }
    }
}

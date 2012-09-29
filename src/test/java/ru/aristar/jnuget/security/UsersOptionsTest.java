package ru.aristar.jnuget.security;

import java.io.InputStream;
import javax.xml.bind.JAXBException;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * Тесты для настроек прав пользователей
 *
 * @author sviridov
 */
public class UsersOptionsTest {

    /**
     * Тест распознавания прав пользователей
     *
     * @throws JAXBException ошибка преобразования XML
     */
    @Test
    public void testParseUserOptions() throws JAXBException {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/security/nuget-users.xml");
        //WHEN
        UsersOptions result = UsersOptions.parse(inputStream);
        //THEN
        assertThat(result, is(not(nullValue())));
        assertThat(result.getUsers().size(), is(equalTo(2)));
        assertThat(result.getUsers().get(0).getPassword(), is(equalTo("testPassword1")));
        assertThat(result.getUsers().get(1).getPassword(), is(equalTo("testPassword2")));
        assertThat(result.getUsers().get(0).getName(), is(equalTo("testUser1")));
        assertThat(result.getUsers().get(1).getName(), is(equalTo("testUser2")));
        assertThat(result.getUsers().get(0).getApiKey(), is(equalTo("testApiKey")));
        assertThat(result.getUsers().get(1).getApiKey(), is(nullValue()));
        assertThat(result.getUsers().get(0).getRoles().size(), is(equalTo(1)));        
        assertThat(result.getUsers().get(0).getRoles(), is(everyItem(equalTo(Role.Uncnown))));        
    }
}

package ru.aristar.jnuget.security;

import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class UserTest {

    /**
     * @param xmlString строка с XML
     * @return распознанные настройки пользователя
     * @throws JAXBException ошибка разбора XML
     */
    private User parse(String xmlString) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(User.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader stringReader = new StringReader(xmlString);
        return (User) unmarshaller.unmarshal(stringReader);
    }

    /**
     * Проверка распознания пользователя с правами администратора
     *
     * @throws JAXBException некорректный фориат XML
     */
    @Test
    public void testParseAdministratorUser() throws JAXBException {
        String xml = "<user name='testUser1' password='testPassword1' apiKey='testApiKey'>"
                + "<role>jnuget-admin</role>"
                + "</user>";
        User user = parse(xml);
        assertThat(user, is(notNullValue()));
        assertThat(user.getName(), is(equalTo("testUser1")));
        assertThat(user.getPassword(), is(equalTo("testPassword1")));
        assertThat(user.getApiKey(), is(equalTo("testApiKey")));
        assertThat(user.getRoles(), is(notNullValue()));
        assertThat(user.getRoles().size(), is(equalTo(1)));
        assertThat(user.getRoles(), is(everyItem(equalTo(Role.Administrator))));
    }

    /**
     * Проверка распознания пользователя с правами администратора
     *
     * @throws JAXBException некорректный фориат XML
     */
    @Test
    public void testParseUserWithIncorrectRoles() throws JAXBException {
        String xml = "<user name='testUser1' password='testPassword1' apiKey='testApiKey'>"
                + "<role>errorRole1</role>"
                + "<role>errorRole2</role>"
                + "<role>errorRole3</role>"
                + "<role>errorRole4</role>"
                + "</user>";
        User user = parse(xml);
        assertThat(user, is(notNullValue()));
        assertThat(user.getName(), is(equalTo("testUser1")));
        assertThat(user.getPassword(), is(equalTo("testPassword1")));
        assertThat(user.getApiKey(), is(equalTo("testApiKey")));
        assertThat(user.getRoles(), is(notNullValue()));
        assertThat(user.getRoles().size(), is(equalTo(1)));
        assertThat(user.getRoles(), is(everyItem(equalTo(Role.Uncnown))));
    }
}

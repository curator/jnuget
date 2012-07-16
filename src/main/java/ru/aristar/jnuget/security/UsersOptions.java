package ru.aristar.jnuget.security;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Права пользователей
 *
 * @author sviridov
 */
@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.NONE)
public class UsersOptions {

    /**
     * Настройки пользователей
     */
    @XmlElement(name = "user")
    private List<User> users;

    /**
     * @return настройки пользователей
     */
    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    /**
     * @param users настройки пользователей
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Записывает XML представление объекта в поток
     *
     * @param outputStream поток, для записи
     * @throws JAXBException ошибка преобразования в XML
     */
    public void writeToStream(OutputStream outputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(this.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(this, outputStream);
    }

    /**
     * Записывает XML представление объекта в файл
     *
     * @param optionsFile файл для записи
     * @throws JAXBException ошибка преобразования в XML
     * @throws FileNotFoundException файл для сохранения не найден
     */
    public void saveOptions(File optionsFile) throws JAXBException, FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(optionsFile);
        writeToStream(outputStream);
    }

    /**
     * Поиск пользователя по ключу
     *
     * @param apiKey ключ доступа
     * @return пользователь или null
     */
    public User findUser(String apiKey) {
        if (apiKey == null) {
            return null;
        }
        for (User user : getUsers()) {
            if (user.getApiKey() != null && user.getApiKey().equals(apiKey)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Поиск пользователя по имени и паролю
     *
     * @param name имя пользователя
     * @param password пароль пользователя
     * @return пользователь или null
     */
    public User findUser(String name, char[] password) {
        if (name == null || password == null) {
            return null;
        }
        for (User user : getUsers()) {
            if (user.getName() != null && user.getName().equalsIgnoreCase(name)) {
                String stringPassword = new String(password);
                if (user.getPassword() != null && stringPassword.equals(user.getPassword())) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Распознает XML представление объекта из потока
     *
     * @param inputStream поток с данными
     * @return распознанный объект
     * @throws JAXBException ошибка распознавания XML
     */
    public static UsersOptions parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(UsersOptions.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (UsersOptions) unmarshaller.unmarshal(inputStream);
    }

    /**
     * Распознает XML представление объекта из файла
     *
     * @param optionsFile файл с данными
     * @return распознанный объект
     * @throws JAXBException ошибка распознавания XML
     * @throws FileNotFoundException файл с данными не найден
     */
    public static UsersOptions parse(File optionsFile) throws JAXBException, FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(optionsFile);
        return parse(fileInputStream);
    }
}

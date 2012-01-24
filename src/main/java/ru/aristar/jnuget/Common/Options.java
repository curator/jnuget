package ru.aristar.jnuget.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Настройки сервера
 *
 * @author sviridov
 */
@XmlRootElement(name = "Options")
@XmlAccessorType(XmlAccessType.NONE)
public class Options {

    /**
     * Имя файла с настройками
     */
    public static final String DEFAULT_OPTIONS_FILE_NAME = "jnuget.config.xml";
    /**
     * Имя ресурса с настройками по умолчанию
     */
    public static final String DEFAULT_OPTIONS_RESOURCE_NAME = "/jnuget.default.config.xml";
    /**
     * Логгер
     */
    private static Logger logger = LoggerFactory.getLogger(Options.class);
    /**
     * Имя каталога с пакетами
     */
    private String folderName;
    /**
     * Ключ (пароль для публикации пакетов)
     */
    @XmlElement(name = "ApiKey")
    private String apiKey;

    /**
     *
     * @return имя каталога с пакетами
     */
    @XmlElement(name = "FolderName")
    public String getFolderName() {
        return folderName;
    }

    /**
     *
     * @param folderName имя каталога с пакетами
     */
    public void setFolderName(String folderName) {
        //TODO Необходимо изменить место, в котором производится подмена исходной строки
        this.folderName = OptionConverter.replaceVariables(folderName);
    }

    /**
     *
     * @return Ключ (пароль для публикации пакетов)
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey Ключ (пароль для публикации пакетов)
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Сохраняет настройки в файл
     *
     * @param file файл, в который производится сохранение
     * @throws JAXBException ошибка сохранения
     */
    public void saveOptions(File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Options.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(this, file);
    }

    /**
     * Восстанавливает настройки из файла
     *
     * @param file файл с настройками
     * @return настройки
     * @throws JAXBException ошибка распознавания XML
     */
    public static final Options parse(File file) throws JAXBException, FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return parse(fileInputStream);
    }

    /**
     * Восстанавливает настройки из файла
     *
     * @param inputStream поток с настройками
     * @return настройки
     * @throws JAXBException ошибка распознавания XML
     */
    public static final Options parse(InputStream inputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Options.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Options) unmarshaller.unmarshal(inputStream);
    }

    /**
     * Загружает настройки из файла или восстанавливает настройки по умолчанию
     *
     * @return
     */
    public static final Options loadOptions() {
        File file = new File(DEFAULT_OPTIONS_FILE_NAME);
        //Попытка загрузки настроек
        if (file.exists()) {
            try {
                return Options.parse(file);
            } catch (JAXBException | FileNotFoundException e) {
                logger.warn("Ошибка загрузки настроек", e);
            }
        } else {
            logger.warn("Файл настроек не найден");
            InputStream inputStream = Options.class.getResourceAsStream(DEFAULT_OPTIONS_RESOURCE_NAME);
            try {
                Options options = Options.parse(inputStream);
                logger.info("Загружены настройки по умолчанию");
                options.saveOptions(file);
                logger.info("Настройки сохранены в " + file);
                return options;
            } catch (JAXBException e) {
                logger.warn("Ошибка загрузки настроек по умолчанию", e);
            }
        }
        return null;
    }
}

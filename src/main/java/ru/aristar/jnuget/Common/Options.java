package ru.aristar.jnuget.Common;

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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Настройки сервера
 *
 * @author sviridov
 */
@XmlRootElement(name = "options")
@XmlAccessorType(XmlAccessType.NONE)
public class Options {

    /**
     * Если установлено свойство Java машины nuget.home - используется оно,
     * иначе смотрим переменную окружения NUGET_HOME, иначе используем домашнюю
     * папку текущего пользователя
     */
    static {
        String nugetHome = (String) System.getProperties().get("nuget.home");
        String fileSeparator = (String) System.getProperties().get("file.separator");
        if (nugetHome == null) {
            nugetHome = System.getenv("NUGET_HOME");
            if (nugetHome == null) {
                nugetHome = (String) System.getProperties().get("user.home");
                if (!nugetHome.endsWith(fileSeparator)) {
                    nugetHome = nugetHome + fileSeparator;
                }
                nugetHome = nugetHome + ".nuget";
            }
            System.getProperties().setProperty("nuget.home", nugetHome);
        }
    }
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
     * Стратегия фиксации для всего хранилища
     */
    @XmlElement(name = "pushStrategy")
    private PushStrategyOptions strategyOptions;
    /**
     * Список настроек хранилищ
     */
    @XmlElement(name = "storage")
    @XmlElementWrapper(name = "storages")
    private List<StorageOptions> storageOptionsList;

    /**
     * @return Список настроек хранилищ
     */
    public List<StorageOptions> getStorageOptionsList() {
        if (storageOptionsList == null) {
            storageOptionsList = new ArrayList<>();
        }
        return storageOptionsList;
    }

    /**
     * @param storageOptionsList Список настроек хранилищ
     */
    public void setStorageOptionsList(List<StorageOptions> storageOptionsList) {
        this.storageOptionsList = storageOptionsList;
    }

    /**
     * @return Стратегия фиксации для всего хранилища
     */
    public PushStrategyOptions getStrategyOptions() {
        return strategyOptions;
    }

    /**
     * @param strategyOptions Стратегия фиксации для всего хранилища
     */
    public void setStrategyOptions(PushStrategyOptions strategyOptions) {
        this.strategyOptions = strategyOptions;
    }

    /**
     * Сохраняет настройки в файл
     *
     * @param file файл, в который производится сохранение
     * @throws JAXBException ошибка сохранения
     */
    public void saveOptions(File file) throws JAXBException, FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        saveOptions(fileOutputStream);
    }

    public void saveOptions(OutputStream outputStream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Options.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(this, outputStream);
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
        String homeFolderName = (String) System.getProperties().get("nuget.home");
        File homeFolder = new File(homeFolderName);
        homeFolder.mkdirs();
        File file = new File(homeFolder, DEFAULT_OPTIONS_FILE_NAME);
        //Попытка загрузки настроек
        if (file.exists()) {
            try {
                logger.info("Загрузка настроек из файла {}", new Object[]{file.getAbsolutePath()});
                return Options.parse(file);
            } catch (JAXBException | FileNotFoundException e) {
                logger.warn("Ошибка загрузки настроек из файла " + file, e);
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
            } catch (JAXBException | FileNotFoundException e) {
                logger.warn("Ошибка загрузки настроек по умолчанию", e);
            }
        }
        return null;
    }
}

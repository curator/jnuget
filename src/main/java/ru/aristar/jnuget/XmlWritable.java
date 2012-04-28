package ru.aristar.jnuget;

import java.io.OutputStream;
import javax.xml.bind.JAXBException;

/**
 * Интерфейс объекта, способного записать свое XML представление в поток
 *
 * @author sviridov
 */
public interface XmlWritable {

    /**
     * Записывает XML представление объекта в поток
     *
     * @param outputStream поток для записи
     * @throws JAXBException ошибка преобразования объекта в XML
     */
    public void writeXml(OutputStream outputStream) throws JAXBException;
}

package ru.aristar.jnuget;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;

/**
 * Класс использующийся для записи RSS данных в поток
 *
 * @author sviridov
 */
public class XmlStreamingOutput implements StreamingOutput {

    /**
     * RSS данные о пакетах
     */
    private final XmlWritable objectToWrite;

    /**
     * @param objectToWrite данные о пакетах
     */
    public XmlStreamingOutput(XmlWritable objectToWrite) {
        this.objectToWrite = objectToWrite;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
            objectToWrite.writeXml(output);
        } catch (JAXBException e) {
            throw new WebApplicationException(e);
        }
    }
}

package ru.aristar.jnuget;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import ru.aristar.jnuget.rss.PackageFeed;

/**
 * Класс использующийся для записи RSS данных в поток
 *
 * @author sviridov
 */
public class FeedStreamingOutput implements StreamingOutput {

    /**
     * RSS данные о пакетах
     */
    private final PackageFeed packageFeed;

    /**
     * @param packageFeed данные о пакетах
     */
    public FeedStreamingOutput(PackageFeed packageFeed) {
        this.packageFeed = packageFeed;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
            packageFeed.writeXml(output);
        } catch (JAXBException e) {
            throw new WebApplicationException(e);
        }
    }
}

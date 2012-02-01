package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

/**
 *
 * @author sviridov
 */
public class TempNupkgFile extends ClassicNupkg implements AutoCloseable {

    /**
     * Копирует данные из одного канала в другой
     *
     * @param src канал источник
     * @param dest канал назначение
     * @throws IOException ошибка ввода/вывода
     */
    public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    /**
     * Создает временный файл на основе потока
     *
     * @param inputStream поток с данными
     * @return файл с данными
     * @throws IOException ошибка чтения/записи
     */
    private static File createTemporaryFile(InputStream inputStream) throws IOException {
        File file = File.createTempFile("nupkg", "jnuget");
        ReadableByteChannel src = Channels.newChannel(inputStream);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel dest = fileOutputStream.getChannel();
        try {
            fastChannelCopy(src, dest);
        } finally {
            src.close();
            dest.close();
        }
        return file;
    }

    public TempNupkgFile(InputStream inputStream) throws IOException, JAXBException, SAXException, NugetFormatException {
        this(inputStream, new Date());
    }

    public TempNupkgFile(InputStream inputStream, Date updated) throws IOException, JAXBException, SAXException, NugetFormatException {
        super(createTemporaryFile(inputStream));
        this.updated = updated;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void close() throws Exception {
        file.delete();
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setNuspecFile(NuspecFile nuspecFile) {
        this.nuspecFile = nuspecFile;
    }
}

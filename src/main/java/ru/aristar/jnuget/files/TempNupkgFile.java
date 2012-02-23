package ru.aristar.jnuget.files;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBException;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class TempNupkgFile implements Nupkg, AutoCloseable {

    private Hash hash;
    private File file;
    private Date updated;
    private NuspecFile nuspecFile;
    /**
     * Логгер
     */
    protected org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

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
        this.file = createTemporaryFile(inputStream);
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

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        if (hash != null) {
            return hash;
        }

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] buffer = new byte[1024];

        InputStream inputStream = getStream();
        int len = 0;
        while ((len = inputStream.read(buffer)) >= 0) {
            md.update(buffer, 0, len);
        }
        byte[] mdbytes = md.digest();
        hash = new Hash(mdbytes);
        return hash;
    }

    @Override
    public InputStream getStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    @Override
    public NuspecFile getNuspecFile() {
        if (nuspecFile == null) {
            try {
                loadNuspec();
            } catch (IOException | JAXBException | SAXException e) {
                //TODO Добавить выброс exception-а
                logger.warn("Ошибка чтения файла спецификации", e);
            }
        }
        return nuspecFile;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setNuspecFile(NuspecFile nuspecFile) {
        this.nuspecFile = nuspecFile;
    }

    /**
     *
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     */
    private void loadNuspec() throws IOException, JAXBException, SAXException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            loop:
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(NuspecFile.DEFAULT_FILE_EXTENSION)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    while ((len = zipInputStream.read(buffer)) >= 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.flush();
                    outputStream.close();
                    nuspecFile = NuspecFile.Parse(outputStream.toByteArray());
                    break loop;
                }
            }
        }
    }

    @Override
    public String getFileName() {
        return getId() + "." + getVersion().toString() + DEFAULT_EXTENSION;
    }

    @Override
    public Long getSize() {
        if (file == null) {
            return null;
        }
        return file.length();
    }

    @Override
    public String getId() {
        return getNuspecFile().getId();
    }

    @Override
    public Version getVersion() {
        return getNuspecFile().getVersion();
    }
}

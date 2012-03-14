package ru.aristar.jnuget.files;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class TempNupkgFile implements Nupkg, AutoCloseable {

    /**
     * Хеш пакета
     */
    private Hash hash;
    /**
     * файл пакета
     */
    private File file;
    /**
     * Дата обновления пакета
     */
    private Date updated;
    /**
     * Файл спецификации пакета
     */
    private NuspecFile nuspecFile;
    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

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
     * @param targetFile файл, в который необходимо скопировать пакет
     * @return файл с данными
     * @throws IOException ошибка чтения/записи
     * @throws NoSuchAlgorithmException в системе не установлен алгоритм для
     * расчета значения HASH
     */
    private static Hash copyDataAndCalculateHash(InputStream inputStream, File targetFile) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        try (ReadableByteChannel src = Channels.newChannel(digestInputStream);
                FileChannel dest = fileOutputStream.getChannel();) {
            fastChannelCopy(src, dest);
            return new Hash(digestInputStream.getMessageDigest().digest());
        }
    }

    /**
     * Создает пакет NuGet из потока
     *
     * @param inputStream поток с пакетом
     * @throws IOException ошибка чтения данных
     * @throws NugetFormatException поток не содержит пакет NuGet или формат
     * пакета - не соответствует стандарту
     */
    public TempNupkgFile(InputStream inputStream) throws IOException, NugetFormatException {
        this(inputStream, new Date());
    }

    /**
     * Создает пакет NuGet из потока
     *
     * @param inputStream поток с пакетом
     * @param updated дата обновления пакета
     * @throws IOException ошибка чтения данных
     * @throws NugetFormatException поток не содержит пакет NuGet или формат
     * пакета - не соответствует стандарту
     */
    public TempNupkgFile(InputStream inputStream, Date updated) throws IOException, NugetFormatException {
        try {
            this.file = File.createTempFile("nupkg", "jnuget");
            this.hash = copyDataAndCalculateHash(inputStream, this.file);
            this.updated = updated;
        } catch (NoSuchAlgorithmException ex) {
            throw new NugetFormatException("Не удается подсчитать HASH пакета", ex);
        }
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
    public Hash getHash() {
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

    @Override
    public void load() throws IOException {
    }
}

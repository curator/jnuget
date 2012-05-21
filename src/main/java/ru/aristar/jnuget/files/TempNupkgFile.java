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
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import ru.aristar.jnuget.Version;

/**
 * Пакет, хранящий данные во временном файле
 *
 * @author sviridov
 */
public class TempNupkgFile extends ClassicNupkg implements Nupkg, AutoCloseable {

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
        MessageDigest messageDigest = MessageDigest.getInstance(Hash.ALGORITHM_NAME);
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
    public void close() throws Exception {
        file.delete();
    }

    @Override
    public String getId() {
        if (this.id == null) {
            try {
                this.id = getNuspecFile().getId();
            } catch (NugetFormatException e) {
                logger.error("Ошибка чтения идентификатора пакета", e);
            }
        }
        return this.id;
    }

    @Override
    public Version getVersion() {
        if (this.version == null) {
            try {
                this.version = getNuspecFile().getVersion();
            } catch (NugetFormatException e) {
                logger.error("Ошибка чтения версии пакета", e);
            }
        }
        return this.version;
    }

    @Override
    public void load() throws IOException {
        getId();
        getVersion();
    }
}

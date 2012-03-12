package ru.aristar.jnuget.files;

import java.io.*;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/**
 * Контрольная сумма
 *
 * @author Unlocker
 */
public class Hash {

    private byte[] digest;

    public Hash(byte[] digest) {
        this.digest = digest;
    }

    @Override
    public String toString() {
        return DatatypeConverter.printBase64Binary(digest);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Hash)) {
            return false;
        }

        Hash other = (Hash) obj;
        if (this.digest.length != other.digest.length) {
            return false;
        }

        for (int i = 0; i < this.digest.length; i++) {
            if (digest[i] != other.digest[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.digest);
        return hash;
    }

    /**
     * Сохраняет HASH в поток
     *
     * @param outputStream поток, в который будет записан HASH
     * @throws IOException ошибка записи в поток
     */
    public void saveTo(OutputStream outputStream) throws IOException {
        Writer writer = new OutputStreamWriter(outputStream);
        writer.write(this.toString());
        writer.flush();
        outputStream.flush();
    }

    /**
     * Сохраняет Hash в файл
     *
     * @param hashFile файл, в который будет сохранен HASH
     * @throws IOException ошибка записи на диск
     */
    public void saveTo(File hashFile) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(hashFile)) {
            saveTo(fileOutputStream);
        }
    }

    public static Hash parse(String base64digest) {
        return new Hash(DatatypeConverter.parseBase64Binary(base64digest));
    }

    /**
     * Метод читает хеш из файла
     *
     * @param file файл с хешем
     * @return хеш пакета
     * @throws IOException ошибка чтения пакета
     */
    public static Hash parse(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            char[] buffer = new char[(int) file.length()];
            int charCount = fileReader.read(buffer);
            if (charCount == 0) {
                throw new IOException("Прочитан пустой файл с контрольной суммой.");
            }
            return parse(String.valueOf(buffer));
        }
    }
}

package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.EnumSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.activation.UnsupportedDataTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class ClassicNupkg implements Nupkg {

    /**
     * Файл спецификации пакета
     */
    protected NuspecFile nuspecFile;
    /**
     * Дата обновления пакета
     */
    protected Date updated;
    /**
     * файл пакета
     */
    protected File file;
    /**
     * Версия пакета
     */
    protected Version version;
    /**
     * Идентификатор пакета
     */
    protected String id;
    /**
     * Хеш пакета
     */
    protected Hash hash;
    /**
     * Логгер
     */
    protected transient Logger logger;

    /**
     * Конструктор используется в классах потомках для пустой инициализации
     */
    protected ClassicNupkg() {
    }

    /**
     * @param file файл пакета
     * @throws NugetFormatException файл пакета не соответствует формату NuGet
     */
    public ClassicNupkg(File file) throws NugetFormatException {
        this.file = file;
        parse(file.getName());
    }

    /**
     * Возвращает локальный файл пакета на диске
     *
     * @return локальный файл
     */
    public File getLocalFile() {
        return file;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public NuspecFile getNuspecFile() {
        if (nuspecFile == null) {
            try {
                nuspecFile = loadNuspec(getStream());
            } catch (NugetFormatException | IOException e) {
                //TODO Добавить выброс exception-а
                getLogger().warn("Ошибка чтения файла спецификации", e);
            }
        }
        return nuspecFile;
    }

    @Override
    public Date getUpdated() {
        if (updated == null) {
            this.updated = new Date(file.lastModified());
        }
        return updated;
    }

    @Override
    public InputStream getStream() throws IOException {
        if (file == null || !file.exists()) {
            throw new UnsupportedDataTypeException("Не найден файл пакета");
        } else {
            return new FileInputStream(file);
        }
    }

    @Override
    public String getFileName() {
        return getId() + "." + getVersion().toString() + DEFAULT_EXTENSION;
    }

    @Override
    public Hash getHash() throws NoSuchAlgorithmException, IOException {
        if (hash != null) {
            return hash;
        }

        MessageDigest md = MessageDigest.getInstance(Hash.ALGORITHM_NAME);
        try (InputStream inputStream = getStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) >= 0) {
                md.update(buffer, 0, len);
            }
            byte[] mdbytes = md.digest();
            hash = new Hash(mdbytes);
            return hash;
        }
    }

    @Override
    public Long getSize() {
        if (file == null) {
            return null;
        }
        return file.length();
    }

    /**
     * Проверяет является ли имя файла пакета валидным
     *
     * @param name имя файла
     * @return true, если имя файла соответствует формату
     */
    public static boolean isValidFileName(String name) {
        if (name == null) {
            return false;
        }
        return name.toLowerCase().endsWith(Nupkg.DEFAULT_EXTENSION);
    }

    /**
     * Разбирает строку названия файла пакета
     *
     * @param filename название файла
     * @throws NugetFormatException некорректный формат имени файла
     */
    private void parse(String filename) throws NugetFormatException {
        if (filename == null || filename.isEmpty()) {
            throw new NugetFormatException("Неправильный формат строки " + filename);
        }
        Matcher matcher = parser.matcher(filename);
        if (!matcher.matches()) {
            throw new NugetFormatException("Неправильный формат строки " + filename);
        } else {
            try {
                id = matcher.group(1);
                version = Version.parse(matcher.group(2));
            } catch (Exception ex) {
                throw new NugetFormatException("Неправильный формат строки", ex);
            }
        }
    }

    /**
     * ZIP вложение является XML спецификацией Nuspec
     *
     * @param entry ZIP вложение
     * @return true если вложение соответствует вложению со спецификацией
     */
    protected boolean isNuspecZipEntry(ZipEntry entry) {
        return !entry.isDirectory() && entry.getName().endsWith(NuspecFile.DEFAULT_FILE_EXTENSION);
    }

    /**
     * Извлечение файла спецификации из потока с пакетом NuPkg
     *
     * @param packageStream поток с пакетом
     * @return файл спецификации
     * @throws IOException ошибка чтения
     * @throws NugetFormatException XML в архиве пакета не соответствует
     * спецификации NuGet
     */
    protected NuspecFile loadNuspec(InputStream packageStream) throws IOException, NugetFormatException {
        try (ZipInputStream zipInputStream = new ZipInputStream(packageStream);) {
            ZipEntry entry;
            do {
                entry = zipInputStream.getNextEntry();
            } while (entry != null && !isNuspecZipEntry(entry));
            if (entry == null) {
                return null;
            }
            return NuspecFile.Parse(zipInputStream);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + id + ":" + version + '}';
    }

    @Override
    public int hashCode() {
        int intHash = 7;
        try {
            intHash = 61 * intHash + Objects.hashCode(this.getHash());
        } catch (NoSuchAlgorithmException | IOException e) {
            intHash = 61 * intHash + Objects.hashCode(this.id) + Objects.hashCode(this.version);
        }
        return intHash;
    }

    @Override
    public void load() throws IOException {
        try {
            this.getHash();
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }
    /**
     * Выражение разбора строки имени файла
     */
    private final static Pattern parser =
            Pattern.compile("^(.+?)\\.(" + Version.VERSION_FORMAT + ")" + Nupkg.DEFAULT_EXTENSION + "$");

    /**
     * @return the logger
     */
    protected Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

    @Override
    public EnumSet<Frameworks> getTargetFramework() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

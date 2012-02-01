package ru.aristar.jnuget.files;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.activation.UnsupportedDataTypeException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
public class ClassicNupkg implements Nupkg {

    protected NuspecFile nuspecFile;
    protected Date updated;
    protected File file;
    protected Version version;
    protected String id;

    public ClassicNupkg(File file) throws JAXBException, IOException, SAXException, NugetFormatException {
        this.file = file;
        this.updated = new Date(file.lastModified());
        parse(file.getName());
    }

    @Override
    public NuspecFile getNuspecFile() {
        return nuspecFile;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public InputStream getStream() throws IOException {
        if (file != null) {
            return new FileInputStream(file);
        } else {
            throw new UnsupportedDataTypeException("Файл с данными не установлен");
        }
    }

    @Override
    public String getFileName() {
        return getNuspecFile().
                getId() + "."
                + getNuspecFile().getVersion().toString() + DEFAULT_EXTENSION;
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
    public Long getSize() {
        if (file == null) {
            return null;
        }
        return file.length();
    }

    public static boolean isValidFileName(String name) {
        if (name == null) {
            return false;
        }
        return name.toLowerCase().endsWith(ClassicNupkg.DEFAULT_EXTENSION);
    }
    private Hash hash;
    
             
    /**
     * Выражение разбора строки имени файла
     */
    private static Pattern parser =
            Pattern.compile("^(.+?)\\.(" + Version.VERSION_FORMAT + ")" + ClassicNupkg.DEFAULT_EXTENSION + "$");

    /**
     * Разбирает строку названия файла пакета
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
}

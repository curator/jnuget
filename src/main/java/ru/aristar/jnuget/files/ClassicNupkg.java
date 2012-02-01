package ru.aristar.jnuget.files;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.activation.UnsupportedDataTypeException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

/**
 *
 * @author sviridov
 */
public class ClassicNupkg implements Nupkg {

    protected NuspecFile nuspecFile;
    protected Date updated;
    protected File file;

    public ClassicNupkg(InputStream inputStream, Date updated) throws IOException, JAXBException, SAXException {
        this.updated = updated;
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
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

    public ClassicNupkg(File file) throws JAXBException, IOException, SAXException {
        this(new FileInputStream(file), new Date(file.lastModified()));
        this.file = file;
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
}

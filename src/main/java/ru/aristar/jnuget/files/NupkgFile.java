package ru.aristar.jnuget.files;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

/**
 *
 * @author sviridov
 */
public class NupkgFile {

    private NuspecFile nuspecFile;
    private Date updated;
    protected File file;

    public NupkgFile(InputStream inputStream, Date updated) throws IOException, JAXBException, SAXException {
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

    public NupkgFile(File file) throws JAXBException, IOException, SAXException {
        this(new FileInputStream(file), new Date(file.lastModified()));
        this.file = file;
    }

    public NuspecFile getNuspecFile() {
        return nuspecFile;
    }

    public void setNuspecFile(NuspecFile nuspecFile) {
        this.nuspecFile = nuspecFile;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public InputStream getStream() throws IOException {
        if (file != null) {
            return new FileInputStream(file);
        } else {
            return null;
        }
    }

    public String getFileName() {
        return getNuspecFile().
                getId() + "."
                + getNuspecFile().getVersion().toString() + DEFAULT_EXTENSION;
    }

    public String getHash() throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] buffer = new byte[1024];

        InputStream inputStream = getStream();
        if (inputStream == null) {
            return null;
        }
        int len = 0;
        while ((len = inputStream.read(buffer)) >= 0) {
            md.update(buffer, 0, len);
        };
        byte[] mdbytes = md.digest();
        return javax.xml.bind.DatatypeConverter.printBase64Binary(mdbytes);
    }

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
        return name.toLowerCase().endsWith(NupkgFile.DEFAULT_EXTENSION);
    }
    /**
     * Расширение по умолчанию
     */
    public static final String DEFAULT_EXTENSION = ".nupkg";
}

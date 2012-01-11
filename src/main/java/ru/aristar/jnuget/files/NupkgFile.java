package ru.aristar.jnuget.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBException;

/**
 *
 * @author sviridov
 */
public class NupkgFile {

    private NuspecFile nuspecFile;
    private Date updated;
    private File file;

    public NupkgFile(InputStream inputStream, Date updated) throws IOException, JAXBException {
        this.updated = updated;
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            loop:
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".nuspec")) {
                    byte[] buffer = new byte[(int) entry.getSize()];
                    zipInputStream.read(buffer, 0, buffer.length);
                    nuspecFile = NuspecFile.Parse(buffer);
                    break loop;
                }
            }
        }
    }

    public NupkgFile(File file) throws JAXBException, IOException {
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
        return getNuspecFile().getId() + "."
                + getNuspecFile().getVersion().toString() + DEFAULT_EXTENSION;
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

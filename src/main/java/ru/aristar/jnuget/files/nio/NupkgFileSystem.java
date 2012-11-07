package ru.aristar.jnuget.files.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class NupkgFileSystem extends FileSystem {

    /**
     * Поток с исходными данными
     */
    private InputStream sourceStream;
    /**
     * Список вложений
     */
    private List<NupkgPath> pathes = new ArrayList<>();

    /**
     * @param nupkg пакет NuPkg
     * @throws IOException ошибка чтения данных
     */
    public NupkgFileSystem(Nupkg nupkg) throws IOException {
        InputStream stream = nupkg.getStream();
        if (stream.markSupported()) {
            sourceStream = stream;
        } else {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ReadableByteChannel sourceChannel = Channels.newChannel(stream);
            WritableByteChannel targetChanel = Channels.newChannel(arrayOutputStream);
            TempNupkgFile.fastChannelCopy(sourceChannel, targetChanel);
            stream.close();
            sourceStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());
        }
        ZipInputStream zipInputStream = new ZipInputStream(sourceStream);

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            pathes.add(new NupkgPath(this, entry));
        }
    }

    @Override
    public FileSystemProvider provider() {
        return new NupkgFileSystemProvider(this);
    }

    @Override
    public void close() throws IOException {
        sourceStream.close();
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isReadOnly() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSeparator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        ArrayList<Path> result = new ArrayList<>();
        result.add(new NupkgPath(this, "", true));
        return result;
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path getPath(String first, String... more) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected Set<Path> getChildren(Path dir) {
        Set<Path> result = new HashSet<>();
        p:
        for (Path path : pathes) {
            for (Path subPath : path) {
                if (subPath.startsWith(dir) && subPath.getNameCount() == dir.getNameCount() + 1) {
                    result.add(subPath);
                    continue p;
                }
            }
        }
        return result;
    }
}

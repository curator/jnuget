package ru.aristar.jnuget.files.nio;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;

/**
 * Путь
 *
 * @author sviridov
 */
public class NupkgPath implements Path {

    /**
     * Файловая система
     */
    private final NupkgFileSystem fileSystem;
    /**
     * Это директория
     */
    private final boolean isDirectory;

    /**
     * Разрезать строку на пути
     *
     * @param pathString исходная строка пути
     * @return разрезанная на пути строка
     */
    private static List<String> splitPathString(String pathString) {
        if (pathString.equals("")) {
            return Arrays.asList("");
        }
        Iterable<String> source = Splitter.on("/").split(pathString);
        source = Iterables.concat(Arrays.asList(""), source);
        ArrayList<String> result = Lists.newArrayList(source);
        return result;
    }

    /**
     * @return это директория
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * @param fileSystem файловая система
     * @param entry путь
     * @param isDirectory это директория
     */
    public NupkgPath(NupkgFileSystem fileSystem, String entry, boolean isDirectory) {
        this(fileSystem, splitPathString(entry), isDirectory);
    }

    /**
     * @param fileSystem файловая система
     * @param entrys путь разделеный на вложения
     * @param isDirectory это директория
     */
    public NupkgPath(NupkgFileSystem fileSystem, Collection<String> entrys, boolean isDirectory) {
        this.fileSystem = fileSystem;
        this.isDirectory = isDirectory;
        pathLineNames.addAll(entrys);
    }

    /**
     * @param fileSystem это директория
     * @param entry вложение в ZIP архив
     */
    public NupkgPath(NupkgFileSystem fileSystem, ZipEntry entry) {
        this(fileSystem, entry.getName(), entry.isDirectory());
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public boolean isAbsolute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path getRoot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path getFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNameCount() {
        return pathLineNames.size();
    }

    @Override
    public Path getName(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean startsWith(Path other) {
        Iterator<Path> otherIterator = other.iterator();
        Iterator<Path> thisIterator = this.iterator();

        while (otherIterator.hasNext() && thisIterator.hasNext()) {
            Path o1 = otherIterator.next();
            Path o2 = thisIterator.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2))) {
                return false;
            }
        }
        return !(otherIterator.hasNext() || thisIterator.hasNext()) || thisIterator.hasNext();
    }

    @Override
    public boolean startsWith(String other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean endsWith(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean endsWith(String other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path normalize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path resolve(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path resolve(String other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path resolveSibling(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path resolveSibling(String other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path relativize(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public URI toUri() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path toAbsolutePath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File toFile() {
        String fileName = "/" + Joiner.on("/").join(pathLineNames);
        return new File(fileName);
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Path> iterator() {
        return new NugetPathIterator();
    }

    @Override
    public int compareTo(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "nupkg://" + Joiner.on("/").join(pathLineNames.subList(1, pathLineNames.size()));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.fileSystem);
        hash = 79 * hash + Objects.hashCode(this.pathLineNames);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NupkgPath other = (NupkgPath) obj;
        if (!Objects.equals(this.fileSystem, other.fileSystem)) {
            return false;
        }
        if (!Objects.equals(this.pathLineNames, other.pathLineNames)) {
            return false;
        }
        return true;
    }
    /**
     * Разрезанная на пути строка
     */
    private LinkedList<String> pathLineNames = new LinkedList<>();

    /**
     * Итератор по вложеным путям
     */
    private class NugetPathIterator implements Iterator<Path> {

        /**
         * Текущая позиция
         */
        private int pos = 0;

        @Override
        public boolean hasNext() {
            return pos < pathLineNames.size();
        }

        @Override
        public Path next() {
            List<String> result = pathLineNames.subList(0, pos + 1);
            boolean isThis = (pathLineNames.size() - 1) == pos;
            ++pos;
            boolean isResultIsDir = isThis ? isDirectory : true;
            NupkgPath path = new NupkgPath(fileSystem, result, isResultIsDir);
            return path;
        }

        @Override
        public void remove() {
        }
    }
}

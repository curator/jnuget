package ru.aristar.jnuget.files.nio;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 *
 * @author sviridov
 */
class NupkgFileAttributes implements BasicFileAttributes {

    /**
     * Путь для которого указываются атрибуты
     */
    private final NupkgPath nupkgPath;

    /**
     * @param nupkgPath пакет для которого создается файловая система
     */
    NupkgFileAttributes(NupkgPath nupkgPath) {
        this.nupkgPath = nupkgPath;
    }

    @Override
    public FileTime lastModifiedTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileTime lastAccessTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileTime creationTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRegularFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDirectory() {
        return nupkgPath.isDirectory();
    }

    @Override
    public boolean isSymbolicLink() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isOther() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object fileKey() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

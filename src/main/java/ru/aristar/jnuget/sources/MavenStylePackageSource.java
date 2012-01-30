package ru.aristar.jnuget.sources;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.NuspecFile;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 * Хранилище пакетов, имитирующее структуру хранилища Maven.
 *
 * @author unlocker
 */
public class MavenStylePackageSource implements PackageSource {

    /**
     * Корневая папка, в которой расположены пакеты
     */
    private File rootFolder;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Стратегия помещения пакета в хранилище
     */
    private PushStrategy strategy;

    /**
     * Конструктор по умолчанию
     */
    public MavenStylePackageSource() {
    }

    /**
     * @param rootFolder папка с пакетами
     */
    public MavenStylePackageSource(File rootFolder) {
        this.rootFolder = rootFolder;
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
    }

    @Override
    public Collection<NupkgFile> getPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<NupkgFile> getLastVersionPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<NupkgFile> getPackages(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<NupkgFile> getPackages(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getLastVersionPackage(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getPackage(String id, Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NupkgFile getPackage(String id, Version version, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean pushPackage(NupkgFile nupkgFile, String apiKey) throws IOException {
        if (!getPushStrategy().canPush(nupkgFile, apiKey)) {
            return false;
        }
        File packageFolder = verifyPackageDestination(rootFolder, nupkgFile.getNuspecFile());
        // Открывает временный файл, копирует его в место постоянного хранения.
        File tmpDest = new File(packageFolder, nupkgFile.getFileName() + ".tmp");
        File finalDest = new File(packageFolder, nupkgFile.getFileName());
        FileChannel dest;
        try (ReadableByteChannel src = Channels.newChannel(nupkgFile.getStream())) {
            dest = new FileOutputStream(tmpDest).getChannel();
            TempNupkgFile.fastChannelCopy(src, dest);
        }
        dest.close();
        // TODO добавить распаковку nuspec и контрольной суммы.
        if (!tmpDest.renameTo(finalDest)) {
            throw new IOException("Не удалось переименовать файл " + tmpDest
                    + " в " + finalDest);
        }
        return true;
    }

    private File verifyPackageDestination(File rootFolder, NuspecFile source) {
        String id = source.getId();
        Version version = source.getVersion();
        File packageFolder = new File(rootFolder, id.toLowerCase());
        if (!packageFolder.exists()) {
            packageFolder.mkdir();
        }
        File versionFolder = new File(packageFolder, version.toString());
        if (!versionFolder.exists()) {
            versionFolder.mkdir();
        }
        return versionFolder;
    }

    private boolean extractNuspec(File packageFolder, NuspecFile nuspec) throws JAXBException, IOException {
        File nuspecFile = new File(packageFolder, "nuspec.xml");
        try (OutputStream outputStream = new FileOutputStream(nuspecFile)) {
            nuspec.saveTo(outputStream);
        }
        return true;
    }

    private boolean extractHash(File packageFolder, String hash) throws FileNotFoundException, IOException {
        File hashFile = new File(packageFolder, "hash.sha512");
        FileChannel dest = new FileOutputStream(hashFile).getChannel();
        ByteBuffer buffer = ByteBuffer.wrap(hash.getBytes());
        int write = dest.write(buffer);
        if (write != buffer.capacity()) {
            throw new IOException("Не удалось записать контрольную сумму в файл " + hashFile);
        }
        return true;
    }

    @Override
    public PushStrategy getPushStrategy() {
        if (strategy == null) {
            strategy = new SimplePushStrategy(false);
        }
        return strategy;
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        this.strategy = strategy;
    }
}

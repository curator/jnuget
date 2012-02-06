package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class IndexedFilePackageSource implements PackageSource {

    /**
     * Индекс пакетов
     */
    private volatile Index index = new Index();
    /**
     * Индексируемый источник пакетов
     */
    protected FilePackageSource packageSource = new FilePackageSource();
    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Поторк, обновляющий индекс
     */
    private class RefreshIndexThread implements Runnable {

        @Override
        public void run() {
            try {
                refreshIndex();
            } catch (Exception e) {
                logger.error("Ошибка оновления индекса для хранилища " + getFolderName(), e);
            }
        }
    }

    private void refreshIndex() {
        Collection<Nupkg> packages = packageSource.getPackages();
        Index newIndex = new Index();
        newIndex.putAll(packages);
        this.index = newIndex;
    }

    public Index getIndex() {
        return index;
    }

    @Override
    public Collection<Nupkg> getPackages() {
        ArrayList<Nupkg> result = new ArrayList<>();
        Iterator<Nupkg> iterator = getIndex().getAllPackages();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    @Override
    public Collection<Nupkg> getLastVersionPackages() {
        ArrayList<Nupkg> result = new ArrayList<>();
        Iterator<Nupkg> iterator = getIndex().getLastVersions();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Nupkg> getPackages(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getLastVersionPackage(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getLastVersionPackage(String id, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getPackage(String id, Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nupkg getPackage(String id, Version version, boolean ignoreCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean pushPackage(Nupkg file, String apiKey) throws IOException {
        boolean result = packageSource.pushPackage(file, apiKey);
        if (result) {
            index.put(file);
        }
        return result;

    }

    @Override
    public PushStrategy getPushStrategy() {
        return packageSource.getPushStrategy();
    }

    @Override
    public void setPushStrategy(PushStrategy strategy) {
        packageSource.setPushStrategy(strategy);
    }

    public Thread setFolderName(String folderName) {
        packageSource.setFolderName(folderName);
        Thread thread = new Thread(new RefreshIndexThread());
        thread.start();
        return thread;
    }

    public String getFolderName() {
        return packageSource.getFolderName();
    }
}

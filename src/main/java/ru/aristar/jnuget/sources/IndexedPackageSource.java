package ru.aristar.jnuget.sources;

import it.sauronsoftware.cron4j.InvalidPatternException;
import it.sauronsoftware.cron4j.Scheduler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.text.MessageFormat.format;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.ModifyStrategy;

/**
 * Индексируемое хранилище пакетов
 *
 * @author sviridov
 */
public class IndexedPackageSource implements PackageSource<Nupkg> {

    /**
     * Индекс пакетов
     */
    private volatile Index index = null;
    /**
     * Индексируемый источник пакетов
     */
    private PackageSource<? extends Nupkg> packageSource;
    /**
     * Имя файла индекса
     */
    private File indexStoreFile = null;
    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Таймер обновления индекса
     */
    private Timer timer;
    /**
     * Интервал обновления индекса храниилща
     */
    private Integer refreshInterval;
    /**
     * Семафор, использующийся при помещении пакета в хранилище
     */
    private final Semaphore pushSemaphore = new Semaphore(1);
    //TODO RefreshIndex semaphore
    /**
     * Индекс находистя в процессе обновления
     */
    private volatile boolean refreshIndexInProgress = false;
    /**
     * Очередь пакетов, ожидающих помещение в хранилище
     */
    private BlockingQueue<Nupkg> newPackageQueue = new LinkedBlockingQueue<>();
    /**
     * Планировщик обновления индекса.
     */
    private Scheduler scheduler;

    @Override
    public void refreshPackage(Nupkg nupkg) {
        packageSource.refreshPackage(nupkg);
    }

    @Override
    public String getName() {
        return packageSource.getName();
    }

    @Override
    public void setName(String storageName) {
        packageSource.setName(storageName);
    }

    /**
     * Поток, обновляющий индекс
     */
    private class RefreshIndexThread extends TimerTask {

        /**
         * Основной метод потока, обновляющий индекс
         */
        @Override
        public void run() {
            try {
                if (refreshIndexInProgress) {
                    logger.info("Предыдущая задача обновления индекса не успела завершится");
                    return;
                }
                refreshIndex();
            } catch (Exception e) {
                logger.error("Ошибка оновления индекса для хранилища " + packageSource, e);
            }
        }
    }

    /**
     * Перечитывает индекс хранилища
     *
     * @throws InterruptedException обновление индекса было прервано
     * @throws IOException ошибка чтения/записи пакета
     */
    private void refreshIndex() throws InterruptedException, IOException {
        synchronized (this) {
            logger.info("Инициировано обновление индекса хранилища {}", new Object[]{packageSource});
            refreshIndexInProgress = true;
            try {
                Collection<? extends Nupkg> packages = packageSource.getPackages();
                Index newIndex = new Index();
                for (Nupkg nupkg : packages) {
                    try {
                        nupkg.load();
                        newIndex.put(nupkg);
                    } catch (IOException e) {
                        logger.warn("Ошибка инициализации пакета", e);
                    }
                }
                logger.info("Добавление в индекс, ожидающих пакетов");
                try {
                    pushSemaphore.acquire();
                    logger.info(format("Ожидает добавления {0} пакетов", newPackageQueue.size()));
                    while (!newPackageQueue.isEmpty()) {
                        final Nupkg nupkg = newPackageQueue.poll();
                        packageSource.pushPackage(nupkg);
                        newIndex.put(nupkg);
                    }
                } finally {
                    pushSemaphore.release();
                }
                this.index = newIndex;
                if (indexStoreFile != null) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(indexStoreFile)) {
                        index.saveTo(fileOutputStream);
                    } catch (Exception e) {
                        logger.warn("Не удалось сохранить локальную копию индекса", e);
                    }
                }
                logger.info("Обновление индекса хранилища {} завершено. Обнаружено {} пакетов",
                        new Object[]{packageSource, index.size()});
                this.notifyAll();
            } finally {
                refreshIndexInProgress = false;
            }
        }
    }

    /**
     * Возвращает индекс хранилища
     *
     * @return индекс хранилища
     */
    public Index getIndex() {
        if (index == null) {
            try {
                synchronized (this) {
                    while (index == null) {
                        logger.warn("Индекс не создан, ожидание создания индекса");
                        this.wait();
                    }
                }
            } catch (InterruptedException e) {
                logger.error("Ожидание загрузки индекса прервано. Остановка потока");
            }
        }
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
            Nupkg nupkg = iterator.next();
            if (nupkg != null) {
                result.add(nupkg);
            } else {
                logger.warn("Индекс для хранилища {} содержит null пакеты", new Object[]{packageSource});
            }
        }
        return result;
    }

    @Override
    public Collection<Nupkg> getPackages(String id) {
        return getIndex().getPackageById(id);
    }

    @Override
    public Nupkg getLastVersionPackage(String id) {
        return getIndex().getLastVersion(id);
    }

    @Override
    public Nupkg getPackage(String id, Version version) {
        return getIndex().getPackage(id, version);
    }

    @Override
    public boolean pushPackage(Nupkg file) throws IOException {
        try {
            pushSemaphore.acquire();
            if (refreshIndexInProgress) {
                if (!packageSource.getPushStrategy().canPush()) {
                    return false;
                }
                newPackageQueue.add(file);
                return true;
            } else {
                boolean result = packageSource.pushPackage(file);
                if (result) {
                    Nupkg localFile = packageSource.getPackage(file.getId(), file.getVersion());
                    getIndex().put(localFile);
                }
                return result;
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        } finally {
            pushSemaphore.release();
        }
    }

    @Override
    public ModifyStrategy getPushStrategy() {
        return packageSource.getPushStrategy();
    }

    @Override
    public void setPushStrategy(ModifyStrategy strategy) {
        packageSource.setPushStrategy(strategy);
    }

    @Override
    public void removePackage(Nupkg nupkg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return источник пакетов, подлежащий индексированию
     */
    public PackageSource<? extends Nupkg> getUnderlyingSource() {
        return packageSource;
    }

    /**
     * @param packageSource источник пакетов, подлежащий индексированию
     */
    public void setUnderlyingSource(PackageSource<? extends Nupkg> packageSource) {
        setUnderlyingSource(packageSource, false);
    }

    /**
     * @param packageSource источник пакетов, подлежащий индексированию
     * @param forseRescan инициировать пересканирование хранилища
     * @return поток обновления индекса
     */
    public Thread setUnderlyingSource(PackageSource<? extends Nupkg> packageSource, boolean forseRescan) {
        this.packageSource = packageSource;
        if (forseRescan) {
            Thread thread = new Thread(new IndexedPackageSource.RefreshIndexThread());
            thread.start();
            return thread;
        } else {
            return null;
        }
    }

    /**
     * Устанавливает интервал обновления информации о хранилище
     *
     * @param interval интервал обновления информации в минутах
     */
    public void setRefreshInterval(Integer interval) {
        this.refreshInterval = interval;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (refreshInterval == null) {
            return;
        }
        final long intervalMs = refreshInterval * 60000;
        RefreshIndexThread indexThread = new IndexedPackageSource.RefreshIndexThread();
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(indexThread, intervalMs, intervalMs);
    }

    /**
     * Запланировать обновление информации о хранилище
     *
     * @param cronString строка cron
     */
    public void setCronSheduller(String cronString) {
        try {
            if (scheduler != null) {
                scheduler.stop();
            }
            scheduler = new Scheduler();
            scheduler.schedule(cronString, new RefreshIndexThread());
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        } catch (IllegalStateException | InvalidPatternException e) {
            logger.error(format("Не удалось запланировать обновление индекса с параметрами {0}", cronString), e);
        }
    }

    /**
     * @return интервал обновления индекса хранилища
     */
    public Integer getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * @param indexStoreFile файл для хранения индекса
     */
    public void setIndexStoreFile(File indexStoreFile) {
        this.indexStoreFile = indexStoreFile;

        if (this.indexStoreFile != null && this.indexStoreFile.exists()) {
            logger.info("Для хранилища {} обнаружен локально сохраненный файл "
                    + "индекса", new Object[]{packageSource});
            try (FileInputStream fileInputStream = new FileInputStream(this.indexStoreFile)) {
                this.index = Index.loadFrom(fileInputStream);
                logger.info("Индекс загружен в память из локального файла \"{}\"", new Object[]{this.indexStoreFile});
                Iterator<Nupkg> iterator = this.index.getAllPackages();
                while (iterator.hasNext()) {
                    Nupkg nupkg = iterator.next();
                    this.packageSource.refreshPackage(nupkg);
                }
                logger.info("Индекс просканирован. Обнаружено {} пакетов",
                        new Object[]{index.size()});
            } catch (Exception e) {
                logger.warn("Не удалось прочитать локально сохраненный индекс", e);
            }
        } else {
            logger.info("Локально сохраненный файл индекса для хранилища {} не "
                    + "обнаружен", new Object[]{packageSource});
        }
    }

    /**
     * @return файл для хранения индекса
     */
    public File getIndexStoreFile() {
        return indexStoreFile;
    }

    /**
     * Создает файл для хранения индекса
     *
     * @param path путь к каталогу, в котором требуется создать файл
     * @param storageName имя хранилища
     * @return файл для хранения индекса
     */
    public static File getIndexSaveFile(File path, String storageName) {
        return new File(path, storageName + ".idx");
    }
}

package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.text.MessageFormat.format;
import java.util.*;
import static org.quartz.CronScheduleBuilder.*;
import org.quartz.CronTrigger;
import org.quartz.Job;
import static org.quartz.JobBuilder.*;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import static org.quartz.TriggerBuilder.*;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.ModifyStrategy;

/**
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
     * Ключ триггера обновления хранилища
     */
    private TriggerKey triggerKey;

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
    private class RefreshIndexThread extends TimerTask implements Job {

        /**
         * Основной метод потока, обновляющий индекс
         */
        @Override
        public void run() {
            try {
                refreshIndex();
            } catch (Exception e) {
                logger.error("Ошибка оновления индекса для хранилища " + packageSource, e);
            }
        }

        /**
         * Основной метод
         *
         * @param context контекст исполнения
         * @throws JobExecutionException ошибка исполнения
         */
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            run();
        }
    }

    /**
     * Перечитывает индекс хранилища
     */
    private void refreshIndex() {
        synchronized (this) {
            logger.info("Инициировано обновление индекса хранилища {}", new Object[]{packageSource});
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
        synchronized (this) {
            boolean result = packageSource.pushPackage(file);
            if (result) {
                Nupkg localFile = packageSource.getPackage(file.getId(), file.getVersion());
                getIndex().put(localFile);
            }
            return result;
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
     * @return поток обновления индекса
     */
    public Thread setUnderlyingSource(PackageSource<? extends Nupkg> packageSource) {
        this.packageSource = packageSource;
        Thread thread = new Thread(new IndexedPackageSource.RefreshIndexThread());
        thread.start();
        return thread;
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
            SchedulerFactory factory = new org.quartz.impl.StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
            if (triggerKey != null) {
                scheduler.unscheduleJob(triggerKey);
            }

            JobDetail refreshIndexJob = newJob(RefreshIndexThread.class).build();

            triggerKey = TriggerKey.triggerKey(packageSource.toString(), PackageSource.class.getName());
            CronTrigger trigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(cronString)).forJob(refreshIndexJob).build();
            scheduler.scheduleJob(trigger);
        } catch (Exception e) {
            logger.error(format("Не удалось запланировать обновление индекса с параметрами {0}", cronString), e);
            triggerKey = null;
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
                logger.info("Индекс загружен в память");
                Iterator<Nupkg> iterator = this.index.getAllPackages();
                while (iterator.hasNext()) {
                    Nupkg nupkg = iterator.next();
                    this.packageSource.refreshPackage(nupkg);
                }
                logger.info("Индекс загружен из локального файла {}. Обнаружено "
                        + "{} пакетов", new Object[]{this.indexStoreFile, index.size()});
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

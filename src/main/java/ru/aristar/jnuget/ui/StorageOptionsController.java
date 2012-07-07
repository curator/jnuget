package ru.aristar.jnuget.ui;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static java.text.MessageFormat.format;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.Common.StorageOptions;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.IndexedPackageSource;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Контроллер настроек хранилища
 *
 * @author sviridov
 */
@ManagedBean(name = "storageOptions")
@RequestScoped
public class StorageOptionsController implements Serializable {

    /**
     * Идентификатор хранилища
     */
    private Integer storageId;
    /**
     * Хранилище
     */
    private PackageSource<? extends Nupkg> packageSource;
    /**
     * Индексирующий декоратор (или null, если хранилище не индексируется)
     */
    private IndexedPackageSource indexDecorator;

    /**
     * Инициализация хранилища
     */
    public void init() {
        packageSource = PackageSourceFactory.getInstance().getPackageSource().getSources().get(storageId);
        if (packageSource instanceof IndexedPackageSource) {
            indexDecorator = (IndexedPackageSource) packageSource;
            packageSource = indexDecorator.getUnderlyingSource();
        }
    }

    /**
     * @return идентификатор хранилища
     */
    public Integer getStorageId() {
        return storageId;
    }

    /**
     * @param storageId идентификатор хранилища
     */
    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    /**
     * @return имя класса хранилища
     */
    public String getClassName() {
        return packageSource.getClass().getCanonicalName();
    }

    /**
     * @param className имя класса хранилища
     * @throws NugetFormatException ошибка создания хранилища указанного класса
     */
    public void setClassName(String className) throws
            NugetFormatException {
        try {
            Class<?> packageSourceClass = Class.forName(className);
            Constructor<?> constructor = packageSourceClass.getConstructor();
            Object result = constructor.newInstance();
            if (!(result instanceof PackageSource)) {
                throw new NugetFormatException(format("Класс {0} не является {1}", className, PackageSource.class.getName()));
            }
            packageSource = (PackageSource) result;
        } catch (NoSuchMethodException | ClassNotFoundException |
                InstantiationException |
                IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e) {
            throw new NugetFormatException(format("Ошибка создания объекта класса {0}", className), e);
        }
    }

    /**
     * @return индексируемое или нет хранилище
     */
    public boolean isIndexed() {
        return indexDecorator != null;
    }

    /**
     * @param value индексируемое или нет хранилище
     */
    public void setIndexed(boolean value) {
        indexDecorator = new IndexedPackageSource();
        indexDecorator.setUnderlyingSource(packageSource);
    }

    /**
     * @return интервал обновления индекса хранилища
     */
    public Integer getRefreshInterval() {
        if (indexDecorator == null) {
            return null;
        }
        return indexDecorator.getRefreshInterval();
    }

    /**
     * @param value интервал обновления индекса хранилища
     */
    public void setRefreshInterval(Integer value) {
        if (indexDecorator == null) {
            return;
        }
        indexDecorator.setRefreshInterval(value);
    }

    /**
     * @return настройки хранилища
     */
    private StorageOptions getStorageOptions() {
        return PackageSourceFactory.getInstance().getOptions().getStorageOptionsList().get(storageId);
    }

    /**
     * @return параметры настройки хранилища
     */
    public DataModel<Map.Entry<String, String>> getStorageProperties() {
        ArrayList<Map.Entry<String, String>> data = new ArrayList<>(getStorageOptions().getProperties().entrySet());
        return new ListDataModel<>(data);
    }
}

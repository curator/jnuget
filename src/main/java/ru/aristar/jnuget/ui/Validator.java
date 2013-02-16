package ru.aristar.jnuget.ui;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Класс, реализующий проверки корректности параметров
 *
 * @author sviridov
 */
@ManagedBean(name = "validator")
@RequestScoped
public class Validator {

    /**
     * Проверка корректности имени хранилища
     *
     * @param context контекст сервиса
     * @param component компонент пользовательского интерфейса
     * @param object объект, подлежащий валидации
     */
    public void validateStorageId(FacesContext context, UIComponent component, Object object) {
        if (object == null || !(object instanceof String)) {
            sendErrorCode(context, 404);
            return;
        }
        String newStorageName = (String) object;
        if (PackageSourceFactory.getInstance().getPublicPackageSource(newStorageName) == null) {
            sendErrorCode(context, 404);
        }
    }

    /**
     * Отправляет код ошибки
     *
     * @param context контекст JSF
     * @param errorCode код ошибки
     */
    private void sendErrorCode(FacesContext context, int errorCode) {
        context.getExternalContext().setResponseStatus(errorCode);
        context.responseComplete();
    }
}

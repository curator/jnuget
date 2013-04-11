package ru.aristar.jnuget.ui;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Компонент переключения локали
 *
 * @author sviridov
 */
@ManagedBean(name = "language")
@SessionScoped
public class LanguageBean implements Serializable {

    /**
     * Локаль, установленная пользователем
     */
    private volatile Locale currentLocale;

    /**
     * Изменить язык
     *
     * @param languageName имя языка
     */
    public void changeLanguage(String languageName) {
        if (Strings.isNullOrEmpty(languageName)) {
            return;
        }
        switch (languageName.toLowerCase()) {
            case "en": {
                currentLocale = Locale.ENGLISH;
                break;
            }
            case "ru": {
                currentLocale = new Locale("RU");
                break;
            }
        }
        setUserLocale();
    }

    /**
     * Установить локаль выбранную пользователем
     */
    public void setUserLocale() {
        if (currentLocale == null) {
            return;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot().setLocale(currentLocale);
    }
}

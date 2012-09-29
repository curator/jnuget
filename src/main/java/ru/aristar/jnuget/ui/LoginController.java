package ru.aristar.jnuget.ui;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import ru.aristar.jnuget.security.Role;

/**
 * Контроллер формы авторизации
 *
 * @author sviridov
 */
@ManagedBean(name = "login")
@SessionScoped
public class LoginController {

    /**
     * Логгер
     */
    private Logger logger = Logger.getLogger(this.getClass());
    /**
     * Имя пользователя
     */
    private String userName;
    /**
     * Пароль пользователя
     */
    private String password;
    /**
     * Залогирован ли пользователь
     */
    private boolean loggedIn = false;

    /**
     * @return залогирован ли пользователь
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @return имя пользователя
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName имя пользователя
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Войти в систему
     *
     * @return страница переадресации (список источников или страница
     * авторизации)
     */
    public String login() {
        loggedIn = false;
        final FacesContext context = FacesContext.getCurrentInstance();
        try {
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            request.login(userName, password);
            if (request.isUserInRole(Role.GuiUser.getName())) {
                loggedIn = true;
                return "sourceManager";
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Недостаточно прав", null));
            return "failure";
        } catch (ServletException e) {
            logger.error("Ошибка в процессе авторизации", e);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ошибка в процессе авторизации", null));
            return "failure";
        }
    }

    /**
     * Выход из системы
     *
     * @return индексная страница
     */
    public String logout() {
        loggedIn = false;
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
        } catch (ServletException e) {
            logger.error("Ошибка выхода из системы", e);
        }
        return "index";
    }
}

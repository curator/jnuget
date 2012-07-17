package ru.aristar.jnuget.ui;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import ru.aristar.jnuget.security.Roles;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "login")
public class LoginController {

    /**
     * Логгер
     */
    private Logger logger = Logger.getLogger(this.getClass());
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() {
        final FacesContext context = FacesContext.getCurrentInstance();
        try {
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            request.login(userName, password);
            if (request.isUserInRole(Roles.Administrator.getName()) || request.isUserInRole(Roles.GuiUser.getName())) {
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

    public void logout() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
        } catch (ServletException e) {
            logger.error("Ошибка выхода из системы", e);
        }
    }
}

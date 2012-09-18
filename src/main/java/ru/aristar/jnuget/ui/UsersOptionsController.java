package ru.aristar.jnuget.ui;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import ru.aristar.jnuget.security.Role;
import ru.aristar.jnuget.security.User;
import ru.aristar.jnuget.security.UsersOptions;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Контроллер GUI настроек прав доступа
 *
 * @author sviridov
 */
@SessionScoped
@ManagedBean(name = "usersOptions")
public class UsersOptionsController {

    /**
     * @return настройки прав пользователей
     */
    private UsersOptions getUsersOptions() {
        return PackageSourceFactory.getInstance().getOptions().getUserOptions();
    }

    /**
     * @return имена доступных ролей
     */
    public List<String> getRoles() {
        ArrayList<String> roleNames = new ArrayList<>();
        for (Role role : Role.values()) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    /**
     * @return список пользователей в системе
     */
    public DataModel<User> getUsers() {
        List<User> users = getUsersOptions().getUsers();
        return new ListDataModel<>(users);
    }
}

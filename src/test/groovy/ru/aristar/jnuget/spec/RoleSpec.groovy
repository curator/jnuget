package ru.aristar.jnuget.spec

import spock.lang.Specification
import ru.aristar.jnuget.security.Role

/**
 * Спецификация ролей пользователей
 *
 * @author sviridov
 */
class RoleSpec extends spock.lang.Specification{

    /**
     * Роли могут включать в себя другие роли
     */
    def "Roles can include other roles"(){
        expect:
        Role.valueOf(parentRole).contains(Role.valueOf(childRole)) == contains
        where:
        parentRole     |    childRole    |  contains
        "Administrator"| "Administrator" |    true
        "Administrator"| "GuiUser"       |    true
        "Administrator"| "Push"          |    true
        "Administrator"| "Read"          |    true
        "Administrator"| "Delete"        |    true
        "Administrator"| "Delete"        |    true
        "GuiUser"      | "Administrator" |    false
        "GuiUser"      | "Delete"        |    false
        "GuiUser"      | "Read"          |    false
        "GuiUser"      | "Push"          |    false
    }
}


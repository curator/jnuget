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
        parentRole     |  contains  |    childRole    
        "Administrator"|    true    | "Administrator" 
        "Administrator"|    true    | "GuiUser"       
        "Administrator"|    true    | "Push"          
        "Administrator"|    true    | "Read"          
        "Administrator"|    true    | "Delete"        
        "Administrator"|    true    | "Delete"        
        "GuiUser"      |    false   | "Administrator" 
        "GuiUser"      |    false   | "Delete"        
        "GuiUser"      |    false   | "Read"          
        "GuiUser"      |    false   | "Push"          
    }
}


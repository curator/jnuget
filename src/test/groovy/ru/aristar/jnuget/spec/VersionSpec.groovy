package ru.aristar.jnuget.spec

import spock.lang.*
import ru.aristar.jnuget.Version;

/**
 *
 * @author sviridov
 */
class VersionSpec extends spock.lang.Specification{
   
    //TODO Ревизия должна быть null, а не пустой строкой
    def "Version must correct parse string"(){
        expect:
        Version.parse(strVersion).major == major
        Version.parse(strVersion).minor == minor
        Version.parse(strVersion).build == build
        Version.parse(strVersion).revision == revision
        where:
        strVersion  | major | minor | build | revision
        "3"         |   3   |  null |  null |   ""
        "7.33"      |   7   |   33  |  null |   ""
        "1.23.2"    |   1   |   23  |   2   |   ""
        "3.4.5.23"  |   3   |   4   |   5   |   "23"
        "3.4.5-PERT"|   3   |   4   |   5   |  "-PERT"
    }
	
}


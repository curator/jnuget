package ru.aristar.jnuget.spec


import spock.lang.Specification
import ru.aristar.jnuget.files.nuspec.Dependencies
import ru.aristar.jnuget.files.nuspec.Dependency
import ru.aristar.jnuget.files.nuspec.DependenciesGroup

/**
 * Спецификация объединения прямых зависимостей и зависимостей объединенных в 
 * группы
 * @author sviridov
 */
class DependencyGroupSpec extends Specification{
	
    /**
     * Создание тестового списка зависимостей
     * @param size размер списка
     * @return тестовый список зависимостей
     */
    List<Dependency> createDependencies(Integer size){
        if(size == null) return null
        if(size == 0) return []
        def result = new Dependency[size]
        size.times {
            Dependency dependency = Mock()
            result[it] = dependency;
        }
        return result;        
    }
    
    /**
     * Создание тестового списка групп зависимостей
     * @param size размер списка
     * @param countPerGroup число зависимостей в группе
     * @return тестовый список групп зависимостей
     */    
    List<DependenciesGroup> createGroups(Integer size, Integer countPerGroup){
        if(size == null) return null
        if(size == 0) return []
        def result = new DependenciesGroup[size] 
        size.times {
            DependenciesGroup group = new DependenciesGroup()
            group.dependencys = createDependencies(countPerGroup)
            result[it] = group;
        }
        return result;
    }
    
    /**
     * Проверка корректности объединения прямых зависимостей и зависимостей, 
     * объединенных в группы
     */
    def "Dependencies and groups count must correct calculated"(){
        setup:
        Dependencies dependencies = new Dependencies(createDependencies(directCount),createGroups(groupsCount,countPerGroup));
        expect:
        dependencies.getGroups().size() == expectGroupCount
        dependencies.getDependencies().size() == expectDependenciesSize
        where:
        directCount  | groupsCount  |  countPerGroup  | expectGroupCount | expectDependenciesSize
            0        |      1       |       1         |         1        |          1  
            1        |      1       |       1         |         2        |          2  
            1        |      1       |       2         |         2        |          3  
            1        |      2       |       2         |         3        |          5  
            2        |      2       |       2         |         3        |          6  
    }
}


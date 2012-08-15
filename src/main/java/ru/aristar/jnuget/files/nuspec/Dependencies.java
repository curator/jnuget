package ru.aristar.jnuget.files.nuspec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 * Зависимости пакета
 *
 * @author sviridov
 */
public class Dependencies implements Serializable {

    /**
     * Прямые зависимости
     */
    @XmlElement(name = "dependency", namespace = NuspecFile.NUSPEC_XML_NAMESPACE_2011)
    List<Dependency> dependencies;
    /**
     * Группы зависимостей
     */
    @XmlElement(name = "group", namespace = NuspecFile.NUSPEC_XML_NAMESPACE_2011)
    private List<DependenciesGroup> groups;

    /**
     * @return зависимости пакетов, включая те, что в группах
     */
    public List<Dependency> getDependencies() {
        if (dependencies == null) {
            dependencies = new ArrayList<>();
        }
        List<Dependency> result = new ArrayList<>();
        result.addAll(dependencies);
        if (groups != null) {
            for (DependenciesGroup group : groups) {
                result.addAll(group.getDependencys());
            }
        }
        return result;
    }

    /**
     * @return группы зависимостей, включая корневую
     */
    public List<DependenciesGroup> getGroups() {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        if (dependencies != null && !dependencies.isEmpty()) {
            DependenciesGroup rootGroup = new DependenciesGroup();
            rootGroup.setDependencys(dependencies);
            ArrayList<DependenciesGroup> result = new ArrayList<>(groups.size() + 1);
            result.addAll(groups);
            result.add(rootGroup);
            return result;
        } else {
            return groups;
        }
    }

    /**
     * Конструктор по умолчанию
     */
    public Dependencies() {
    }

    /**
     * Конструктор, позволяющий задать значения зависимостей
     *
     * @param dependencies прямые зависимости
     * @param groups группы зависимостей
     */
    public Dependencies(List<Dependency> dependencies, List<DependenciesGroup> groups) {
        this.dependencies = dependencies;
        this.groups = groups;
    }
}

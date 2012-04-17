package ru.aristar.jnuget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
class QueryExecutor {

    //TODO filter=((((((tolower(Id) eq 'neolant.projectwise.api') or (tolower(Id) eq 'neolant.projectwise.api')) or (tolower(Id) eq 'neolant.projectwise.controls')) or (tolower(Id) eq 'neolant.projectwise.isolationlevel')) or (tolower(Id) eq 'neolant.projectwise.isolationlevel.implementation')) or (tolower(Id) eq 'nlog')) or (tolower(Id) eq 'postsharp') and isLatestVersion
    /**
     * Получение списка пакетов из хранилиза
     *
     * @param packageSource хранилище пакетов
     * @param filter фильтр пакетов
     * @param searchTerm условие поиска
     * @return коллекция пакетов
     */
    public Collection<Nupkg> execQuery(PackageSource<Nupkg> packageSource, final String filter, final String searchTerm) {
        Collection<Nupkg> nupkgs = execQuery(packageSource, filter);
        if (searchTerm == null || searchTerm.equals("")) {
            return nupkgs;
        }
        ArrayList<Nupkg> result = new ArrayList<>();
        for (Nupkg nupkg : nupkgs) {
            if (nupkg.getId().toLowerCase().contains(searchTerm)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    public Collection<Nupkg> execQuery(PackageSource<Nupkg> packageSource, final String filter) {
        if (filter == null || "".equals(filter)) {
            return packageSource.getPackages();
        }
        if (filter.toLowerCase().startsWith("tolower(id) eq")) {
            Pattern pattern = Pattern.compile("(\\w+)\\((\\w+)\\)\\s+(\\w+)\\s+'?([^']+)'?");
            Matcher matcher = pattern.matcher(filter);
            if (matcher.find()) {
                String function = matcher.group(1);
                String field = matcher.group(2);
                String condition = matcher.group(3);
                String value = matcher.group(4);
                return packageSource.getPackages(value, true);
            }
        } else if (filter.toLowerCase().startsWith("islatestversion")) {
            return packageSource.getLastVersionPackages();
        }
        return packageSource.getPackages();
    }
}

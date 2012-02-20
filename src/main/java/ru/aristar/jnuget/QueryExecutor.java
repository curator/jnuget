package ru.aristar.jnuget;

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

    //TODO filter=IsLatestVersion, orderBy=DownloadCount desc,Id, skip=0, top=30, searchTerm='', targetFramework='net40'
    
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

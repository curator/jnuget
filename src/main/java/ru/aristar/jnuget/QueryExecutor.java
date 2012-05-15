package ru.aristar.jnuget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.Framework;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.query.Expression;
import ru.aristar.jnuget.query.QueryLexer;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class QueryExecutor {

    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Удаляет некорректные символы из условия поиска
     *
     * @param sourceValue условие поиска
     * @return нормализованное условие поиска
     */
    private String normaliseTerm(String sourceValue) {
        if (sourceValue == null) {
            return null;
        }
        return sourceValue.replaceAll("['\"]", "").toLowerCase();
    }

    /**
     * Проверка - подходят ли требуемые фреймворки к предоставляемым
     *
     * @param requiredFrameworks требуемые фреймворки
     * @param packageFrameworks фреймворки предоставляемые пакетом
     * @return true, если подходит
     */
    private boolean isCorrectFramework(EnumSet<Framework> requiredFrameworks, EnumSet<Framework> packageFrameworks) {
        return packageFrameworks.isEmpty() || !Collections.disjoint(requiredFrameworks, packageFrameworks);
    }

    /**
     * Получение списка пакетов из хранилища
     *
     * @param packageSource хранилище пакетов
     * @param filter фильтр пакетов
     * @param searchTerm условие поиска
     * @param targetFramework список фреймворков, для которых предназначен пакет
     * @return коллекция пакетов
     */
    public Collection<? extends Nupkg> execQuery(PackageSource<Nupkg> packageSource,
            final String filter, final String searchTerm, final String targetFramework) {
        Collection<? extends Nupkg> nupkgs = execQuery(packageSource, filter, searchTerm);
        EnumSet<Framework> requiredFrameworks = Framework.parse(normaliseTerm(targetFramework));
        if (requiredFrameworks.containsAll(EnumSet.allOf(Framework.class))) {
            return nupkgs;
        }
        ArrayList<Nupkg> result = new ArrayList<>();
        for (Nupkg nupkg : nupkgs) {
            EnumSet<Framework> packageFrameworks = nupkg.getTargetFramework();
            if (isCorrectFramework(requiredFrameworks, packageFrameworks)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    /**
     * Получение списка пакетов из хранилища
     *
     * @param packageSource хранилище пакетов
     * @param filter фильтр пакетов
     * @param searchTerm условие поиска
     * @return коллекция пакетов
     */
    protected Collection<? extends Nupkg> execQuery(PackageSource<Nupkg> packageSource,
            final String filter, final String searchTerm) {
        Collection<? extends Nupkg> nupkgs = execQuery(packageSource, filter);
        final String normSearchTerm = normaliseTerm(searchTerm);
        if (normSearchTerm == null || normSearchTerm.matches("\\s*")) {
            return nupkgs;
        }
        ArrayList<Nupkg> result = new ArrayList<>();
        for (Nupkg nupkg : nupkgs) {
            if (nupkg.getId().toLowerCase().contains(normSearchTerm)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    /**
     * Получение списка пакетов из хранилища
     *
     * @param packageSource хранилище пакетов
     * @param filter фильтр пакетов
     * @return коллекция пакетов
     */
    protected Collection<? extends Nupkg> execQuery(PackageSource<Nupkg> packageSource, final String filter) {
        if (filter == null || filter.isEmpty()) {
            return packageSource.getPackages();
        }
        try {
            QueryLexer queryLexer = new QueryLexer();
            Expression expression = queryLexer.parse(filter);
            return expression.execute(packageSource);
        } catch (NugetFormatException e) {
            logger.warn("Ошибка разбора запроса пакетов", e);
            return packageSource.getPackages();
        }
    }
}

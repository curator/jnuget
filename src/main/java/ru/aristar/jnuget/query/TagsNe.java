package ru.aristar.jnuget.query;

import java.util.ArrayList;
import java.util.Collection;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Выражение сравнения идентификатора без учета регистра
 *
 * @author sviridov
 */
public class TagsNe extends AbstractExpression {

    /**
     * Описание пакета
     */
    private String tags;

    /**
     * @param tags описание пакета
     */
    public TagsNe(String tags) {
        if (tags == null || tags.isEmpty() || tags.equalsIgnoreCase("null")) {
            this.tags = null;
        } else {
            this.tags = tags;
        }
    }

    @Override
    public Collection<? extends Nupkg> execute(PackageSource<? extends Nupkg> packageSource) {
        ArrayList<Nupkg> result = new ArrayList<>();
        for (Nupkg nupkg : packageSource.getPackages()) {
            if (accept(nupkg)) {
                result.add(nupkg);
            }
        }
        return result;
    }

    /**
     * Создает выражение сравнения идентификатора без учета регистра
     *
     * @param tokens очередь токенов
     * @return выражение сравнения идентификатора без учета регистра
     * @throws NugetFormatException токены не соответствуют формату запроса
     * NuGet
     */
    public static TagsNe parse(java.util.Queue<java.lang.String> tokens) throws NugetFormatException {
        assertToken(tokens.poll(), "ne");
        TagsNe expression = new TagsNe(tokens.poll());
        return expression;
    }

    /**
     * @return описание пакета
     */
    public String getDescription() {
        return tags;
    }

    /**
     * @param description описание пакета
     */
    public void setDescription(String description) {
        this.tags = description;
    }

    @Override
    public boolean hasFilterPriority() {
        return true;
    }

    @Override
    public boolean accept(Nupkg nupkg) {
        try {
            if (tags == null) {
                return !nupkg.getNuspecFile().getTags().isEmpty();
            }
            for (String tag : nupkg.getNuspecFile().getTags()) {
                if (tag.equalsIgnoreCase(tags)) {
                    return true;
                }
            }
            return false;
        } catch (NugetFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Tags ne " + tags.toLowerCase();
    }
}

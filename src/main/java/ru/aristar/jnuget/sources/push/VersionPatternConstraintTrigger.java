package ru.aristar.jnuget.sources.push;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 * Стратегия, разрешающая публикацию пакета с версией, соответстыующей шаблону
 *
 * @author sviridov
 */
public class VersionPatternConstraintTrigger implements BeforeTrigger {

    /**
     * Шаблон версии
     */
    private Pattern pattern;

    /**
     * @return Шаблон версии
     */
    public String getPattern() {
        return pattern.toString();
    }

    /**
     * @param pattern Шаблон версии
     */
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean doAction(Nupkg nupkg, PackageSource<? extends Nupkg> packageSource) throws NugetPushException {
        Version version = nupkg.getVersion();
        if (version == null) {
            return false;
        }
        String strVersion = version.toString();
        Matcher matcher = pattern.matcher(strVersion);
        return matcher.matches();
    }
}

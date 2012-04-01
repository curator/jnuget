package ru.aristar.jnuget.sources.push;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;

/**
 * Стратегия, разрешающая публикацию пакета с версией, соответстыующей шаблону
 *
 * @author sviridov
 */
public class VersionPatternPushStrategy implements PushStrategy {

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
    public boolean canPush(Nupkg nupkgFile, String apiKey) {
        Version version = nupkgFile.getVersion();
        if (version == null) {
            return false;
        }
        String strVersion = version.toString();
        Matcher matcher = pattern.matcher(strVersion);
        return matcher.matches();
    }

    @Override
    public List<PushTrigger> getBeforeTriggers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<PushTrigger> getAftherTriggers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

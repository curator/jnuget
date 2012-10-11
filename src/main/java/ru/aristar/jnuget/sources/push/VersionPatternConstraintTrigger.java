package ru.aristar.jnuget.sources.push;

import static java.text.MessageFormat.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.ui.descriptors.Property;

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
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @param pattern Шаблон версии
     */
    @Property
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean doAction(Nupkg nupkg, PackageSource<? extends Nupkg> packageSource) throws NugetPushException {
        Version version = nupkg.getVersion();
        if (version == null) {
            logger.info("Публикация пакета запрещена. Версия не указана");
            return false;
        }
        String strVersion = version.toString();
        Matcher matcher = pattern.matcher(strVersion);
        final boolean result = matcher.matches();
        if (!result) {
            logger.info(format("Публикация пакета запрещена. Версия {0} не "
                    + "соответствует формату", strVersion));
        }
        return result;
    }
}

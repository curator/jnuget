package ru.aristar.jnuget.files;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.util.EnumSet;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Преобразователь списка сборок к строке с разделителями
 */
public class AssemblyTargetFrameworkAdapter extends XmlAdapter<String, EnumSet<Framework>> {

    /**
     * Логгер
     */
    private static Logger logger = LoggerFactory.getLogger(AssemblyTargetFrameworkAdapter.class);
    /**
     * Разделитель фреймворков в строке
     */
    private static final String FRAMEWORKS_DELIMETER = ", ";

    @Override
    public String marshal(EnumSet<Framework> frameworks) throws Exception {
        if (frameworks == null || frameworks.isEmpty()) {
            return null;
        }
        String result = Joiner.on(FRAMEWORKS_DELIMETER).join(frameworks);
        return result;
    }

    @Override
    public EnumSet<Framework> unmarshal(String farmeworks) throws Exception {
        if (Strings.isNullOrEmpty(farmeworks)) {
            return null;
        }
        String[] names = farmeworks.split(FRAMEWORKS_DELIMETER);
        EnumSet<Framework> result = EnumSet.noneOf(Framework.class);
        for (String name : names) {
            try {
                final Framework framework = Framework.getByFullName(name);
                if (framework != null) {
                    result.add(framework);
                }
            } catch (Exception e) {
                logger.warn(java.text.MessageFormat.format("Ошибка добавления фреймворка \"{0}\"", name), e);
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
}

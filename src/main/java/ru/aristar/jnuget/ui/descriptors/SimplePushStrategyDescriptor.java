package ru.aristar.jnuget.ui.descriptors;

import java.util.ArrayList;
import java.util.List;
import ru.aristar.jnuget.sources.push.SimplePushStrategy;

/**
 *
 * @author sviridov
 */
public class SimplePushStrategyDescriptor implements ObjectDescriptor<SimplePushStrategy> {

    @Override
    public Class<? extends SimplePushStrategy> getObjectClass() {
        return SimplePushStrategy.class;
    }

    @Override
    public List<ObjectProperty> getProperties() {
        ArrayList<ObjectProperty> result = new ArrayList<>();
        try {
            ObjectProperty property = new ObjectProperty(
                    SimplePushStrategy.class,
                    "Разрешена или нет публикация",
                    "isAllow",
                    "setAllow");
            result.add(property);
        } catch (NoSuchMethodException e) {
        }
        return result;
    }
}

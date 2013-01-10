package ru.aristar.jnuget.common;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Адаптер свойств хранилища
 *
 * @author sviridov
 */
public class PropertiesTypeAdapter extends XmlAdapter<Properties, Multimap<String, String>> {

    @Override
    public Multimap<String, String> unmarshal(Properties properties) throws Exception {
        Multimap<String, String> result = HashMultimap.create();
        for (Property property : properties.getProperties()) {
            result.put(property.name, property.value);
        }
        return result;
    }

    @Override
    public Properties marshal(Multimap<String, String> map) throws Exception {
        Properties result = new Properties();
        for (String key : map.keySet()) {
            Collection<String> values = map.get(key);
            if (values == null || values.isEmpty()) {
                continue;
            }
            for (String value : values) {
                result.getProperties().add(new Property(key, value));
            }
        }
        return result;
    }
}

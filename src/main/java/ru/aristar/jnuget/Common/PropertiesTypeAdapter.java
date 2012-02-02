package ru.aristar.jnuget.Common;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author sviridov
 */
public class PropertiesTypeAdapter extends XmlAdapter<Properties, Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(Properties properties) throws Exception {
        Map result = new HashMap();
        for (Property property : properties.getProperties()) {
            result.put(property.name, property.value);
        }
        return result;
    }

    @Override
    public Properties marshal(Map<String, String> map) throws Exception {
        Properties result = new Properties();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.getProperties().add(new Property(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}

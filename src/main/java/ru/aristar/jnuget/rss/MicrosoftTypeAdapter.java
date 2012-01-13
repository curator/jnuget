package ru.aristar.jnuget.rss;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author sviridov
 */
public class MicrosoftTypeAdapter extends XmlAdapter<String, MicrosoftTypes> {

    @Override
    public MicrosoftTypes unmarshal(String string) throws Exception {
        return MicrosoftTypes.parse(string);
    }

    @Override
    public String marshal(MicrosoftTypes type) throws Exception {
        return type == null ? null : type.toString();
    }
}

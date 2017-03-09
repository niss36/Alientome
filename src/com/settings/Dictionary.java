package com.settings;

import com.util.I18N;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {

    private final String defaultMapping;
    private final Map<Object, String> mappings;

    Dictionary() {
        this("settings.defaultDictionary", new HashMap<>());
    }

    private Dictionary(String defaultMappingUnlocalized, Map<Object, String> mappings) {

        this.defaultMapping = defaultMappingUnlocalized;
        this.mappings = mappings;
    }

    public static Dictionary parseXML(Element dictionaryNode, Class<?> type) {

        String defaultMapping = dictionaryNode.getAttribute("defaultMapping");

        NodeList entries = dictionaryNode.getElementsByTagName("entry");

        Map<Object, String> mappings = new HashMap<>(entries.getLength());

        for (int i = 0; i < entries.getLength(); i++) {

            Element entry = (Element) entries.item(i);
            Object key = ValueFilter.get(type, entry.getAttribute("key"));
            String mapping = entry.getAttribute("mapping");

            mappings.put(key, mapping);
        }

        return new Dictionary(defaultMapping, mappings);
    }

    public String getMappingFor(Object value) {

        String str = mappings.get(value);

        if (str != null) return I18N.getString(str);

        return I18N.getStringFormatted(defaultMapping, value);
    }
}

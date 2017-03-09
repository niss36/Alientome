package com.settings;

import com.util.listeners.ValueListener;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Setting {

    public final String id;
    public final Class<?> valueType;
    public final ValueFilter filter;
    public final Dictionary dictionary;
    private final List<ValueListener> listeners = new ArrayList<>();

    private Setting(String id, Class<?> valueType, ValueFilter filter, Dictionary dictionary) {
        this.id = id;
        this.valueType = valueType;
        this.filter = filter;
        this.dictionary = dictionary;
    }

    public static Setting parseXML(Element settingNode, List<Setting> editableList) throws ClassNotFoundException {

        String id = settingNode.getAttribute("id");
        String typeClass = settingNode.getAttribute("type");
        Class<?> valueType = Class.forName(typeClass);
        boolean editable = Boolean.parseBoolean(settingNode.getAttribute("editable"));

        ValueFilter filter = null;
        Dictionary dictionary = null;

        NodeList settingNodeChildren = settingNode.getChildNodes();
        for (int i = 0; i < settingNodeChildren.getLength(); i++) {

            Node child = settingNodeChildren.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {

                if (child.getNodeName().equals("valueFilter")) {

                    child = child.getChildNodes().item(1);
                    if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {

                        Element valueFilterImplNode = (Element) child;
                        filter = ValueFilter.parseXML(valueFilterImplNode, valueType);
                    }

                } else if (child.getNodeName().equals("dictionary")) {

                    Element dictionaryNode = (Element) child;

                    dictionary = Dictionary.parseXML(dictionaryNode, valueType);
                }
            }
        }

        if (filter == null)
            filter = new ValueFilter.Type();

        if (dictionary == null)
            dictionary = new Dictionary();

        Setting setting = new Setting(id, valueType, filter, dictionary);
        if (editable)
            editableList.add(setting);
        return setting;
    }

    @Override
    public String toString() {
        return "Setting [" + id + "; " + valueType.getSimpleName() + "; " + filter + "]";
    }

    void addValueListener(ValueListener listener) {
        listeners.add(listener);
    }

    void notifyListeners(Object newValue) {
        for (ValueListener listener : listeners)
            listener.valueChanged(newValue);
    }
}

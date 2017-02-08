package com.settings;

import com.util.listeners.ValueListener;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class Setting {

    public final String id;
    public final Class<?> valueType;
    public final ValueFilter filter;
    private final List<ValueListener> listeners = new ArrayList<>();

    private Setting(String id, Class<?> valueType, ValueFilter filter) {
        this.id = id;
        this.valueType = valueType;
        this.filter = filter;
    }

    public static Setting parseXML(Element settingNode, List<Setting> editableList) throws ClassNotFoundException {

        String id = settingNode.getAttribute("id");
        String typeName = settingNode.getAttribute("type");
        Class<?> valueType = Class.forName("java.lang." + typeName);
        boolean editable = Boolean.parseBoolean(settingNode.getAttribute("editable"));

        ValueFilter filter = null;

        Node child = settingNode.getChildNodes().item(1);
        if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {

            Element valueFilterNode = (Element) child;
            child = valueFilterNode.getChildNodes().item(1);

            if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {

                Element valueFilterImplNode = (Element) child;
                filter = ValueFilter.parseXML(valueFilterImplNode, valueType);
            }
        }

        if (filter == null)
            filter = new ValueFilter.Type();

        Setting setting = new Setting(id, valueType, filter);
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

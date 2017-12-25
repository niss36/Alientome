package com.alientome.core.settings;

import com.alientome.core.util.WrappedXML;

public class Setting {

    public final String id;
    public final Class<?> valueType;

    private Setting(String id, Class<?> valueType) {
        this.id = id;
        this.valueType = valueType;
    }

    public static Setting parseXML(WrappedXML settingXML) throws ClassNotFoundException {

        String id = settingXML.getAttr("id");
        String typeClass = settingXML.getAttr("type");
        Class<?> valueType = Class.forName(typeClass);

        return new Setting(id, valueType);
    }

    @Override
    public String toString() {
        return "Setting [" + id + " : " + valueType.getSimpleName() + "]";
    }
}

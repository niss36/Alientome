package com.settings;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.Collections;

public interface ValueFilter {

    @SuppressWarnings("unchecked")
    static ValueFilter parseXML(Element filterNode, Class<?> type) {

        ValueFilter filter;

        switch (filterNode.getTagName()) {

            case "range":
                assert type.isAssignableFrom(Number.class);
                Number min = (Number) get(type, filterNode.getAttribute("min"));
                Number max = (Number) get(type, filterNode.getAttribute("max"));
                filter = new Range(min, max);
                break;

            case "list":
                NodeList entries = filterNode.getElementsByTagName("entry");
                Object[] values = new Object[entries.getLength()];
                for (int i = 0; i < values.length; i++) {
                    Element entry = (Element) entries.item(i);
                    values[i] = get(type, entry.getAttribute("value"));
                }
                filter = new List(values);
                break;

            default:
                filter = new Type();
        }

        return filter;
    }

    static Object get(Class<?> type, String input) {

        switch (type.getSimpleName()) {

            case "String":
                return input;

            case "Long":
                return Long.valueOf(input);

            case "Double":
                return Double.parseDouble(input);

            case "Integer":
                return Integer.valueOf(input);

            case "Float":
                return Float.valueOf(input);

            case "Short":
                return Short.valueOf(input);

            case "Byte":
                return Byte.valueOf(input);

            case "Boolean":
                return Boolean.valueOf(input);

            default:
                return null;
        }
    }

    class Type implements ValueFilter {

        @Override
        public String toString() {
            return "Type";
        }
    }

    class Range extends Type {

        public final Number min;
        public final Number max;

        Range(Number min, Number max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return "Range [" + min + " - " + max + "]";
        }
    }

    class List extends Type {

        public final java.util.List values;

        List(Object[] values) {
            this.values = Collections.unmodifiableList(Arrays.asList(values));
        }

        @Override
        public String toString() {
            return "List " + values.toString();
        }
    }
}

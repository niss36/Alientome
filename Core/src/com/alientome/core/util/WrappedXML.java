package com.alientome.core.util;

import com.jcabi.xml.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class WrappedXML implements XML {

    private final XML source;

    public WrappedXML(XML source) {
        this.source = source;
    }

    @Override
    public List<String> xpath(String s) {
        return source.xpath(s);
    }

    @Override
    public List<XML> nodes(String s) {
        return source.nodes(s);
    }

    @Override
    public XML registerNs(String s, Object o) {
        return source.registerNs(s, o);
    }

    @Override
    public XML merge(NamespaceContext namespaceContext) {
        return source.merge(namespaceContext);
    }

    @Override
    public Node node() {
        return source.node();
    }

    public Iterable<WrappedXML> nodesWrapped(String query) {

        List<XML> nodes = nodes(query);

        return () -> new Iterator<WrappedXML>() {

            private final Iterator<XML> nodesIterator = nodes.iterator();

            @Override
            public boolean hasNext() {
                return nodesIterator.hasNext();
            }

            @Override
            public WrappedXML next() {
                return new WrappedXML(nodesIterator.next());
            }
        };
    }

    public WrappedXML getFirst(String query) {

        List<XML> nodes = nodes(query);

        return new WrappedXML(nodes.get(0));
    }

    public String getAttr(String name) {
        return xpath("@" + name).get(0);
    }

    public int getAttrInt(String name) {
        return Integer.parseInt(getAttr(name));
    }

    public double getAttrDouble(String name) {
        return Double.parseDouble(getAttr(name));
    }

    public byte getAttrByte(String name) {
        return Byte.parseByte(getAttr(name));
    }

    public boolean getAttrBoolean(String name) {
        return Boolean.parseBoolean(getAttr(name));
    }

    public <T> T getAttrAs(String name, Function<String, T> converter) {
        return converter.apply(getAttr(name));
    }

    public String getAttributeSafe(String name) {
        return ((Element) node()).getAttribute(name);
    }

    public String getOrDefault(String name, String defaultVal) {
        String attr = getAttributeSafe(name);
        return attr.isEmpty() ? defaultVal : attr;
    }

    public int getOrDefaultInt(String name, int defaultVal) {
        String attr = getAttributeSafe(name);
        return attr.isEmpty() ? defaultVal : Integer.parseInt(attr);
    }

    public double getOrDefaultDouble(String name, double defaultVal) {
        String attr = getAttributeSafe(name);
        return attr.isEmpty() ? defaultVal : Double.parseDouble(attr);
    }

    public boolean getOrDefaultBoolean(String name, boolean defaultVal) {
        String attr = getAttributeSafe(name);
        return attr.isEmpty() ? defaultVal : Boolean.parseBoolean(attr);
    }
}

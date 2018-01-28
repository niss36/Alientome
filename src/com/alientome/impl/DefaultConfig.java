package com.alientome.impl;

import com.alientome.core.Context;
import com.alientome.core.settings.AbstractConfig;
import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DefaultConfig extends AbstractConfig {

    private final String configXML;
    private final String defaultConfig;

    public DefaultConfig(Context context, String configXML, String defaultConfig) {

        super(context);

        this.configXML = configXML;
        this.defaultConfig = defaultConfig;
    }

    @Override
    protected InputStream defaultConfig() {
        return ClassLoader.getSystemResourceAsStream(defaultConfig);
    }

    @Override
    protected File userConfig() {
        return context.getFileManager().getConfig();
    }

    @Override
    protected WrappedXML getXML() throws IOException {
        return Util.parseXMLNew(configXML);
    }
}

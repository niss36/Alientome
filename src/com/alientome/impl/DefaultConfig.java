package com.alientome.impl;

import com.alientome.core.SharedInstances;
import com.alientome.core.settings.AbstractConfig;
import com.alientome.core.util.FileManager;
import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.alientome.core.SharedNames.FILE_MANAGER;

public class DefaultConfig extends AbstractConfig {

    private final String configXML;
    private final String defaultConfig;

    public DefaultConfig(String configXML, String defaultConfig) {

        this.configXML = configXML;
        this.defaultConfig = defaultConfig;
    }

    @Override
    protected InputStream defaultConfig() {
        return ClassLoader.getSystemResourceAsStream(defaultConfig);
    }

    @Override
    protected File userConfig() {
        FileManager manager = SharedInstances.get(FILE_MANAGER);
        return manager.getConfig();
    }

    @Override
    protected WrappedXML getXML() throws IOException {
        return Util.parseXMLNew(configXML);
    }
}

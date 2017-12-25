package com.alientome.impl;

import com.alientome.core.SharedInstances;
import com.alientome.core.keybindings.AbstractInputManager;
import com.alientome.core.util.FileManager;
import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.alientome.core.SharedNames.FILE_MANAGER;

public class DefaultInputManager extends AbstractInputManager {

    private final String keybindingsXML;
    private final String defaultKeybindings;

    public DefaultInputManager(String keybindingsXML, String defaultKeybindings) {

        this.keybindingsXML = keybindingsXML;
        this.defaultKeybindings = defaultKeybindings;
    }

    @Override
    protected InputStream defaultKeybindings() {
        return ClassLoader.getSystemResourceAsStream(defaultKeybindings);
    }

    @Override
    protected File userKeybindings() {
        FileManager manager = SharedInstances.get(FILE_MANAGER);
        return manager.getKeybindings();
    }

    @Override
    protected WrappedXML getXML() throws IOException {
        return Util.parseXMLNew(keybindingsXML);
    }
}

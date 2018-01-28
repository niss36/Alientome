package com.alientome.impl;

import com.alientome.core.Context;
import com.alientome.core.keybindings.AbstractInputManager;
import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DefaultInputManager extends AbstractInputManager {

    private final String keybindingsXML;
    private final String defaultKeybindings;

    public DefaultInputManager(Context context, String keybindingsXML, String defaultKeybindings) {

        super(context);

        this.keybindingsXML = keybindingsXML;
        this.defaultKeybindings = defaultKeybindings;
    }

    @Override
    protected InputStream defaultKeybindings() {
        return ClassLoader.getSystemResourceAsStream(defaultKeybindings);
    }

    @Override
    protected File userKeybindings() {
        return context.getFileManager().getKeybindings();
    }

    @Override
    protected WrappedXML getXML() throws IOException {
        return Util.parseXMLNew(keybindingsXML);
    }
}

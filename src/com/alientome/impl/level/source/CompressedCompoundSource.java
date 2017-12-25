package com.alientome.impl.level.source;

import com.alientome.core.util.FileUtils;
import com.alientome.impl.level.source.uri.URIProvider;
import com.alientome.impl.level.source.uri.ZipURIProvider;
import com.alientome.script.ScriptParser;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class CompressedCompoundSource extends CompoundLevelSource {

    private final URI zipURI;

    public CompressedCompoundSource(ScriptParser parser, File file) {

        super(parser);

        zipURI = FileUtils.toZip(file);
    }

    @Override
    protected URIProvider newProvider() throws IOException {
        return new ZipURIProvider(zipURI);
    }
}

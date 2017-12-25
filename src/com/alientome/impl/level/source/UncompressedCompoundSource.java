package com.alientome.impl.level.source;

import com.alientome.impl.level.source.uri.DirectoryURIProvider;
import com.alientome.impl.level.source.uri.URIProvider;
import com.alientome.script.ScriptParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class UncompressedCompoundSource extends CompoundLevelSource {

    private final Path directory;

    public UncompressedCompoundSource(ScriptParser parser, File directory) {

        super(parser);

        this.directory = directory.toPath();
    }

    @Override
    protected URIProvider newProvider() throws IOException {
        return new DirectoryURIProvider(directory);
    }
}

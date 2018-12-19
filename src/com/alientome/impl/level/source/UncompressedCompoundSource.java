package com.alientome.impl.level.source;

import com.alientome.game.GameContext;
import com.alientome.impl.level.source.uri.DirectoryURIProvider;
import com.alientome.impl.level.source.uri.URIProvider;
import com.alientome.script.ScriptParser;

import java.io.IOException;
import java.nio.file.Path;

public class UncompressedCompoundSource extends CompoundLevelSource {

    private final Path directory;

    public UncompressedCompoundSource(GameContext context, ScriptParser parser, Path directory) {

        super(context, parser);

        this.directory = directory;
    }

    @Override
    protected URIProvider newProvider() throws IOException {
        return new DirectoryURIProvider(directory);
    }
}

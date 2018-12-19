package com.alientome.impl.level.source;

import com.alientome.core.util.FileUtils;
import com.alientome.game.GameContext;
import com.alientome.impl.level.source.uri.URIProvider;
import com.alientome.impl.level.source.uri.ZipURIProvider;
import com.alientome.script.ScriptParser;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class CompressedCompoundSource extends CompoundLevelSource {

    private final URI zipURI;

    public CompressedCompoundSource(GameContext context, ScriptParser parser, Path file) {

        super(context, parser);

        zipURI = FileUtils.toZip(file);
    }

    @Override
    protected URIProvider newProvider() throws IOException {
        return new ZipURIProvider(zipURI);
    }
}

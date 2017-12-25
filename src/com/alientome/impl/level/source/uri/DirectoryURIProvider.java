package com.alientome.impl.level.source.uri;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class DirectoryURIProvider implements URIProvider {

    private final Path directory;

    public DirectoryURIProvider(Path directory) {
        this.directory = directory;
    }

    @Override
    public URI get(String path) {
        return directory.resolve(path).toUri();
    }

    @Override
    public void close() throws IOException {
    }
}

package com.alientome.impl.level.source.uri;

import com.alientome.core.util.FileUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ZipURIProvider implements URIProvider {

    private final Path tmp;

    public ZipURIProvider(URI uri) throws IOException {

        tmp = FileUtils.createTempDirectory("Decompress");

        Map<String, String> env = new HashMap<>();
        env.put("create", "false");
        env.put("encoding", "UTF-8");

        try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
            FileUtils.copyDirectory(fs.getPath("/"), tmp, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public URI get(String path) {
        return tmp.resolve(path).toUri();
    }

    @Override
    public void close() throws IOException {
        FileUtils.deleteDirectory(tmp);
    }
}

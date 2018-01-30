package com.alientome.impl.level.source.uri;

import java.io.Closeable;
import java.net.URI;

public interface URIProvider extends Closeable {

    URI get(String path);
}

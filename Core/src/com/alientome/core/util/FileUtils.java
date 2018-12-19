package com.alientome.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    private static final Path alientomeTmp = Paths.get(System.getProperty("java.io.tmpdir")).resolve("Alientome");

    public static URI toZip(Path f) {
        return URI.create("jar:" + f.toUri().toString());
    }

    public static InputStream openStream(URI uri) throws IOException {
        return uri.toURL().openStream();
    }

    public static Path createTempDirectory(String prefix, String... components) throws IOException {

        Path dir = alientomeTmp;

        for (String component : components)
            dir = dir.resolve(component);
        Files.createDirectories(dir);

        return Files.createTempDirectory(dir, prefix);
    }

    public static Path toTempFile(String prefix, String name) throws IOException {

        Path dir = alientomeTmp.resolve(prefix);

        Files.createDirectories(dir);

        return dir.resolve(name);
    }

    public static void deleteDirectory(Path dir) throws IOException {

        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                if (exc != null)
                    throw exc;

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void copyDirectory(Path source, Path target, CopyOption... options) throws IOException {

        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Path relativePath = source.relativize(file);
                Path targetPath = target.resolve(relativePath.toString());
                Files.createDirectories(targetPath.getParent());

                Files.copy(file, targetPath, options);

                return FileVisitResult.CONTINUE;
            }
        });
    }
}

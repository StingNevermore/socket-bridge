/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package com.nevermore.sbridge.server;

import static java.nio.file.Files.createTempDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ProcessUtil {

    private ProcessUtil() { /* no instance*/ }

    interface Interruptible<T> {
        T run() throws InterruptedException;
    }

    interface InterruptibleVoid {
        void run() throws InterruptedException;
    }

    static <T> T nonInterruptible(Interruptible<T> interruptible) {
        try {
            return interruptible.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }

    static void nonInterruptibleVoid(InterruptibleVoid interruptible) {
        nonInterruptible(() -> {
            interruptible.run();
            return null;
        });
    }

    public static CliProcessInfo cliProcessInfo() {
        Properties props = System.getProperties();
        var sysProps = props.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
        var envVars = Map.copyOf(System.getenv());
        var workingDir = Paths.get("").toAbsolutePath();
        return new CliProcessInfo(sysProps, envVars, workingDir);
    }

    public record CliProcessInfo(Map<String, String> sysProps, Map<String, String> envVars, Path workingDir) {

    }

    public static Path tempDir(CliProcessInfo processInfo) {
        final Path path;
        try {
            if (processInfo.sysProps().get("os.name").startsWith("Windows")) {
                path = Paths.get(processInfo.sysProps().get("java.io.tmpdir"), "sbridge");
                Files.createDirectories(path);
            } else {
                path = createTempDirectory("sbridge-");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return path;
    }
}

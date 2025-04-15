/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package com.nevermore.sbridge.server;

import static com.nevermore.sbridge.constants.CliServerCommunicationsConstants.SERVER_READY_MARKER;
import static com.nevermore.sbridge.server.ProcessUtil.nonInterruptibleVoid;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class ErrorPumpThread extends Thread implements Closeable {
    private final BufferedReader reader;

    private final CountDownLatch readyOrDead = new CountDownLatch(1);
    private volatile boolean ready;
    private volatile IOException ioFailure;

    public ErrorPumpThread(InputStream errInput) {
        super("server-cli[stderr_pump]");
        this.reader = new BufferedReader(new InputStreamReader(errInput, StandardCharsets.UTF_8));
    }

    private void checkForIoFailure() throws IOException {
        IOException failure = ioFailure;
        ioFailure = null;
        if (failure != null) {
            throw failure;
        }
    }

    @Override
    public void close() throws IOException {
        assert !isAlive() : "Pump thread must be drained first";
        checkForIoFailure();
    }

    /**
     * Waits until the server ready marker has been received.
     * <p>
     * {@code true} if successful, {@code false} if a startup error occurred
     *
     * @throws IOException if there was a problem reading from stderr of the process
     */
    boolean waitUntilReady() throws IOException {
        nonInterruptibleVoid(readyOrDead::await);
        checkForIoFailure();
        return ready;
    }

    /**
     * Waits for the stderr pump thread to exit.
     */
    void drain() {
        nonInterruptibleVoid(this::join);
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() && line.charAt(0) == SERVER_READY_MARKER) {
                    ready = true;
                    readyOrDead.countDown();
                }
            }
        } catch (IOException e) {
            ioFailure = e;
        } finally {
            readyOrDead.countDown();
        }
    }
}

/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package com.nevermore.sbridge.server;

import java.io.IOException;

import static com.nevermore.sbridge.server.ProcessUtil.nonInterruptible;

/**
 *
 */
public class ServerProcess {

    // the actual java process of the server
    private final Process jvmProcess;

    // the thread pumping stderr watching for state change messages
    private final ErrorPumpThread errorPump;

    // a flag marking whether the streams of the java subprocess have been closed
    private volatile boolean detached = false;

    public ServerProcess(Process jvmProcess, ErrorPumpThread errorPump) {
        this.jvmProcess = jvmProcess;
        this.errorPump = errorPump;
    }

    /**
     * Return the process id of the server.
     */
    public long pid() {
        return jvmProcess.pid();
    }

    /**
     * Detaches the server process from the current process, enabling the current process to exit.
     *
     * @throws IOException If an I/O error occurred while reading stderr or closing any of the standard streams
     */
    public synchronized void detach() throws IOException {
        errorPump.drain();
        try {
            jvmProcess.getOutputStream().close();
            jvmProcess.getInputStream().close();
            jvmProcess.getErrorStream().close();
            errorPump.close();
        } finally {
            detached = true;
        }
    }

    /**
     * Waits for the subprocess to exit.
     */
    public int waitFor() throws IOException {
        errorPump.drain();
        int exitCode = nonInterruptible(jvmProcess::waitFor);
        errorPump.close();
        return exitCode;
    }
}

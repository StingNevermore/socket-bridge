/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package com.nevermore.sbridge.server;

class ProcessUtil {

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
}

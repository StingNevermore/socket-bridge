package com.nevermore.sbridge.exceptions;

/**
 * @author Snake
 */
public class CliException extends RuntimeException {
    private final int exitCode;
    private final String message;

    public CliException(int exitCode, String message) {
        super(message);
        this.exitCode = exitCode;
        this.message = message;
    }

    public CliException(int exitCode, String message, Throwable cause) {
        super(message, cause);
        this.exitCode = exitCode;
        this.message = message;
    }

    public int exitCode() {
        return exitCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

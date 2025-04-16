package com.nevermore.sbridge;

import static com.nevermore.sbridge.constants.CliServerCommunicationsConstants.SERVER_READY_MARKER;
import static java.io.OutputStream.nullOutputStream;

import java.io.PrintStream;
import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Snake
 */
public class ServerReadyListener implements SpringApplicationRunListener {

    @SuppressWarnings({"unused"})
    public ServerReadyListener(SpringApplication SpringApplication, String[] args) {
        // Constructor
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        // for cli startup
        System.err.println(SERVER_READY_MARKER);
        System.err.flush();
        System.setErr(new PrintStream(nullOutputStream()));
        System.setOut(new PrintStream(nullOutputStream()));
    }
}

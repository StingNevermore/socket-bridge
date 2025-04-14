package com.nevermore.sbridge;

import static com.nevermore.sbridge.constants.CliServerCommunicationsConstants.SERVER_READY_MARKER;

import java.time.Duration;

import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Snake
 */
@Component
public class ServerReadyListener implements SpringApplicationRunListener {

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        // for cli startup
        System.err.print(SERVER_READY_MARKER);
    }
}

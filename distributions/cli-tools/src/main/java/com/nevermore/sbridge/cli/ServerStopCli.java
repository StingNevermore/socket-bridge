package com.nevermore.sbridge.cli;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Snake
 */
@Lazy
@Component
@Command(name = "stop", description = "Stop the socket bridge server", mixinStandardHelpOptions = true)
public class ServerStopCli extends AbstractCli {
    @Override
    public void execute() throws Exception {
        System.out.println("stop");
    }
}

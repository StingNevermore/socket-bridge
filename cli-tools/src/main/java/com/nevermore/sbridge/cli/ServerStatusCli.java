package com.nevermore.sbridge.cli;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Snake
 */
@Lazy
@Component
@Command(name = "status", description = "Get the socket bridge server status", mixinStandardHelpOptions = true)
public class ServerStatusCli extends AbstractCli {
    @Override
    public void execute() throws Exception {
        System.out.println("status");
    }
}

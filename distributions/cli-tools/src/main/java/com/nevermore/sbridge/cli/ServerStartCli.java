package com.nevermore.sbridge.cli;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Snake
 */
@Lazy
@Component
@Command(name = "start", description = "Start the socket bridge server", mixinStandardHelpOptions = true)
public class ServerStartCli extends AbstractCli {
    @Override
    public void execute() throws Exception {
        
    }
}

package com.nevermore.sbridge.cli;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;

/**
 * @author Snake
 */
@Lazy
@Component
@Command(name = "sbridge-cli",
        description = "Socket Bridge CLI.", mixinStandardHelpOptions = true,
        subcommands = {
                ServerStartCli.class,
                ServerStopCli.class,
                ServerStatusCli.class,
        }
)
public class MainCli extends AbstractCli {
    @Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    public void execute() {
        spec.commandLine().usage(outWriter);
    }
}

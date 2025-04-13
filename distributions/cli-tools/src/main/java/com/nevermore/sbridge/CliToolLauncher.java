package com.nevermore.sbridge;

import com.nevermore.sbridge.cli.MainCli;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

/**
 * @author Snake
 */
@SpringBootApplication
public class CliToolLauncher implements CommandLineRunner, ExitCodeGenerator {
    private int exitCode = ExitCode.SUCCESS;

    private final MainCli mainCli;
    private final CommandLine.IFactory factory;

    public CliToolLauncher(MainCli mainCli, CommandLine.IFactory factory) {
        this.mainCli = mainCli;
        this.factory = factory;
    }

    public static void main(String[] args) {
        SpringApplication.run(CliToolLauncher.class, args);
    }

    @Override
    public void run(String... args) {
        var cmd = new CommandLine(mainCli, factory);
        cmd.setUsageHelpWidth(100);
        cmd.setUsageHelpLongOptionsMaxWidth(30);
        cmd.setUsageHelpAutoWidth(true);
        var colorScheme = CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.ON);
        cmd.setColorScheme(colorScheme);
        cmd.setHelpFactory((spec, scheme) -> {
            var help = new CommandLine.Help(spec, scheme);
            spec.usageMessage().customSynopsis("socket-bridge-cli <command> [<args>]\n");
            spec.usageMessage().footer("@|italic For more information, run 'socket-bridge-cli help <command>'.|@");
            return help;
        });
        cmd.setParameterExceptionHandler((ex, exArgs) -> {
            System.err.println("Unknown command: " + exArgs[0]);
            System.out.println();
            cmd.usage(System.out);
            return cmd.getCommandSpec().exitCodeOnInvalidInput();
        });

        this.exitCode = cmd.execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}

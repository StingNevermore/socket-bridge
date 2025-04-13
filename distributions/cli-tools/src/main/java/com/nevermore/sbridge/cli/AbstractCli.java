package com.nevermore.sbridge.cli;

import com.nevermore.sbridge.ExitCode;
import com.nevermore.sbridge.exceptions.CliException;
import com.nevermore.sbridge.exceptions.OptionException;

import java.io.PrintStream;
import java.util.concurrent.Callable;

import static com.nevermore.sbridge.ExitCode.USAGE;
import static picocli.CommandLine.usage;

/**
 * @author Snake
 */
public abstract class AbstractCli implements Callable<Integer> {

    protected PrintStream outWriter;
    protected PrintStream errWriter;

    public AbstractCli() {
        this.outWriter = System.out;
        this.errWriter = System.err;
    }

    @Override
    public Integer call() {
        return cliMain();
    }

    protected int cliMain() {
        try {
            cliMainWithoutErrorHandling();
        } catch (OptionException e) {
            errWriter.println("ERROR: " + e.getMessage());
            usage(this, errWriter);
            return e.exitCode();
        } catch (CliException e) {
            errWriter.println("ERROR: " + e.getMessage() + ", with exit code " + e.exitCode());
            if (e.exitCode() == USAGE) {
                usage(this, System.err);
            }
            return e.exitCode();
        } catch (Throwable e) {
            errWriter.println("Error: " + e.getMessage());
            e.printStackTrace(errWriter);
            return ExitCode.ERROR;
        }
        return ExitCode.SUCCESS;
    }

    protected void cliMainWithoutErrorHandling() throws Exception {
        execute();
    }

    protected abstract void execute() throws Exception;
}

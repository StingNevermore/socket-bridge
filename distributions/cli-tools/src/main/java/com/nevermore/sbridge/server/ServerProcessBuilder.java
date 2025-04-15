package com.nevermore.sbridge.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.nevermore.sbridge.exceptions.CliException;
import com.nevermore.sbridge.server.ProcessUtil.CliProcessInfo;

/**
 * @author Snake
 */
public class ServerProcessBuilder {
    private Path tempDir;
    private CliProcessInfo cliProcessInfo;
    private List<String> jvmOptions;


    public ServerProcessBuilder withTempDir(Path tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    public ServerProcessBuilder withCliProcessInfo(CliProcessInfo cliProcessInfo) {
        this.cliProcessInfo = cliProcessInfo;
        return this;
    }

    public ServerProcessBuilder withJvmOptions(List<String> jvmOptions) {
        this.jvmOptions = jvmOptions;
        return this;
    }

    private Map<String, String> getEnvironment() {
        return new HashMap<>(cliProcessInfo.envVars());
    }

    private String getCommand() {
        var javaHome = FileSystems.getDefault().getPath(cliProcessInfo.sysProps().get("java.home"));

        var isWindows = cliProcessInfo.sysProps().get("os.name").startsWith("Windows");
        return javaHome.resolve("bin").resolve("java" + (isWindows ? ".exe" : "")).toString();
    }

    private static void checkRequiredArgument(Object argument, String argumentName) {
        if (argument == null) {
            throw new IllegalStateException(
                    String.format("'%s' is a required argument and needs to be specified before calling start()",
                            argumentName)
            );
        }
    }

    public ServerProcess start() {
        checkRequiredArgument(tempDir, "tempDir");
        checkRequiredArgument(cliProcessInfo, "processInfo");
        checkRequiredArgument(jvmOptions, "jvmOptions");

        Process jvmProcess = null;
        ErrorPumpThread errorPump;

        boolean success = false;
        try {
            jvmProcess = createProcess(getCommand(), jvmOptions, getEnvironment());
            errorPump = new ErrorPumpThread(jvmProcess.getErrorStream());
            errorPump.start();

            if (!errorPump.waitUntilReady()) {
                int exitCode = jvmProcess.waitFor();
                throw new CliException(exitCode, "Socket Bridge died while starting up");
            }
            success = true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            if (!success && jvmProcess != null && jvmProcess.isAlive()) {
                jvmProcess.destroyForcibly();
            }
        }

        return new ServerProcess(jvmProcess, errorPump);
    }

    private static Process createProcess(
            String command,
            List<String> jvmOptions,
            Map<String, String> environment
    ) throws InterruptedException, IOException {
        var jvmArgs = List.of(
                "-jar", "libs/socket-bridge-server.jar"
        );

        var builder = new ProcessBuilder(
                Stream.concat(Stream.of(command),
                                Stream.concat(jvmOptions.stream(), jvmArgs.stream()))
                        .toList());
        builder.environment().putAll(environment);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        return builder.start();
    }
}

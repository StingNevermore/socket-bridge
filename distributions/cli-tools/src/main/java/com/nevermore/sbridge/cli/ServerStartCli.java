package com.nevermore.sbridge.cli;

import static com.nevermore.sbridge.server.ProcessUtil.cliProcessInfo;
import static com.nevermore.sbridge.server.ProcessUtil.tempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.nevermore.sbridge.ExitCode;
import com.nevermore.sbridge.exceptions.CliException;
import com.nevermore.sbridge.server.JvmOptionsUtils;
import com.nevermore.sbridge.server.ServerProcessBuilder;

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
        startServer();
    }

    private void startServer() throws IOException {
        String cwd = System.getProperty("user.dir");
        Path jvmOptionsFile = Paths.get(cwd).resolve("jvm.options");

        List<String> jvmOptions = new ArrayList<>();
        TreeMap<Integer, String> invalidJvmOptions = new TreeMap<>();
        InputStream is = Files.newInputStream(jvmOptionsFile);
        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(reader);
        JvmOptionsUtils.parse(br, jvmOptions::add, invalidJvmOptions::put);
        if (!invalidJvmOptions.isEmpty()) {
            String messageForInvalidJvmOptions =
                    invalidJvmOptions.entrySet().stream()
                            .map(e -> "line number " + e.getKey() + " : " + e.getValue())
                            .collect(Collectors.joining("\n"));
            throw new CliException(ExitCode.USAGE_JVM_OPTIONS,
                    "Invalid jvm options: " + messageForInvalidJvmOptions);
        }

        var cliProcessInfo = cliProcessInfo();
        var serverProcess = new ServerProcessBuilder()
                .withCliProcessInfo(cliProcessInfo)
                .withTempDir(tempDir(cliProcessInfo))
                .withJvmOptions(jvmOptions)
                .start();

        serverProcess.detach();
    }
}

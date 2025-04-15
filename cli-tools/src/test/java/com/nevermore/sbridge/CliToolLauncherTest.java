package com.nevermore.sbridge;

import com.nevermore.sbridge.cli.ServerStartCli;
import com.nevermore.sbridge.cli.ServerStopCli;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.main.log-startup-info=false",
        "spring.main.banner-mode=off"
})
class CliToolLauncherTest {

    @Autowired
    private CliToolLauncher cliToolLauncher;

    @MockitoBean
    private ServerStartCli serverStartCli;

    @MockitoBean
    private ServerStopCli serverStopCli;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        outContent.reset();
        errContent.reset();
    }

    @Test
    void testHelpCommand() throws Exception {
        // 运行命令行，传入-h参数
        cliToolLauncher.run("-h");

        // 验证输出包含帮助信息
        String output = outContent.toString();
        assertTrue(output.contains("socket-bridge-cli <command> [<args>]"));
        assertTrue(output.contains("For more information, run 'socket-bridge-cli help <command>'"));

        // 验证退出代码为0（成功）
        assertEquals(ExitCode.SUCCESS, cliToolLauncher.getExitCode());
    }

    @Test
    void testStartCommand() throws Exception {
        cliToolLauncher.run("start");
        verify(serverStartCli).call();
        assertEquals(ExitCode.SUCCESS, cliToolLauncher.getExitCode());
    }

    @Test
    void testStopCommand() throws Exception {
        cliToolLauncher.run("stop");
        verify(serverStopCli).call();
        assertEquals(ExitCode.SUCCESS, cliToolLauncher.getExitCode());
    }

    @Test
    void testInvalidCommand() {
        cliToolLauncher.run("invalidCommand");
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Unknown command: invalidCommand"));
        String stdOutput = outContent.toString();
        assertTrue(stdOutput.contains("socket-bridge-cli <command> [<args>]"));
        assertNotEquals(ExitCode.SUCCESS, cliToolLauncher.getExitCode());
    }

    @Test
    void testExitCodePropagation() throws Exception {
        doThrow(new RuntimeException("Test exception")).when(serverStartCli).call();
        cliToolLauncher.run("start");
        assertNotEquals(ExitCode.SUCCESS, cliToolLauncher.getExitCode());
    }
}

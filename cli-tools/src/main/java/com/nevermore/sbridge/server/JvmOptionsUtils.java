package com.nevermore.sbridge.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Snake
 */
public class JvmOptionsUtils {
    private static final Pattern PATTERN =
            Pattern.compile("^-.+$");

    public static void parse(
            final BufferedReader br,
            final Consumer<String> jvmOptionConsumer,
            final BiConsumer<Integer, String> invalidLineConsumer
    ) throws IOException {
        int lineNumber = 0;
        while (true) {
            final String line = br.readLine();
            lineNumber++;
            if (line == null) {
                break;
            }
            if (line.startsWith("#")) {
                // lines beginning with "#" are treated as comments
                continue;
            }
            if (line.matches("\\s*")) {
                // skip blank lines
                continue;
            }
            final Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {
                jvmOptionConsumer.accept(line);
            } else {
                invalidLineConsumer.accept(lineNumber, line);
            }
        }

    }
}

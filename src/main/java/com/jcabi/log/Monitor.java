/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**
 * Stream monitor.
 * @since 0.1
 */
final class Monitor implements Callable<Void> {

    /**
     * Stream to read.
     */
    private final transient InputStream input;

    /**
     * Latch to count down when done.
     */
    private final transient CountDownLatch done;

    /**
     * Buffer to save output.
     */
    private final transient OutputStream output;

    /**
     * Log level.
     */
    private final transient Level level;

    /**
     * Ctor.
     * @param inp Stream to monitor
     * @param latch Count down latch to signal when done
     * @param out Buffer to write to
     * @param lvl Logging level
     */
    Monitor(final InputStream inp, final CountDownLatch latch,
        final OutputStream out, final Level lvl) {
        this.input = inp;
        this.done = latch;
        this.output = out;
        this.level = lvl;
    }

    @Override
    public Void call() throws Exception {
        try (
            BufferedReader reader = new BufferedReader(
                Channels.newReader(
                    Channels.newChannel(this.input), StandardCharsets.UTF_8
                )
            );
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(this.output, StandardCharsets.UTF_8)
            )
        ) {
            while (true) {
                if (Thread.interrupted()) {
                    Logger.debug(
                        VerboseProcess.class,
                        "Explicitly interrupting read from buffer"
                    );
                    break;
                }
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                Logger.log(
                    this.level, VerboseProcess.class,
                    ">> %s", line
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (final ClosedByInterruptException ex) {
            Thread.interrupted();
            Logger.debug(
                VerboseProcess.class,
                "Monitor is interrupted in the expected way"
            );
        } catch (final IOException ex) {
            Logger.error(
                VerboseProcess.class,
                "Error reading from process stream: %[exception]s",
                ex
            );
        } finally {
            this.done.countDown();
        }
        return null;
    }
}

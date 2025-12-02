/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link VerboseProcess}.
 *
 * @since 0.1
 * @todo #18 Locale/encoding problem in two test methods here. I'm not
 *  sure how to fix them, but they should be fixed. They fail on some
 *  machines, while run perfectly on others. They also fail when being
 *  executed from IntelliJ.
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCoupling (200 lines)
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals" })
final class VerboseProcessTest {

    @Test
    @Disabled
    void runsACommandLineScript() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS, "");
        try (VerboseProcess process = new VerboseProcess(
            new ProcessBuilder("echo", "hey \u20ac!").redirectErrorStream(true)
        )) {
            MatcherAssert.assertThat(
                "should contains string '\u20ac!'",
                process.stdout(),
                Matchers.containsString("\u20ac!")
            );
        }
    }

    @Test
    @Disabled
    void echosUnicodeCorrectly() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS, "");
        try (VerboseProcess process = new VerboseProcess(
            new ProcessBuilder(
                "/bin/bash", "-c",
                "echo -n \u0442\u0435\u0441\u0442 | hexdump"
            )
        )) {
            MatcherAssert.assertThat(
                "should contains string '0000000 d1 82 d0 b5 d1 81 d1 82'",
                process.stdout(),
                Matchers.containsString("0000000 d1 82 d0 b5 d1 81 d1 82")
            );
        }
    }

    @Test
    void runsACommandLineScriptWithException() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS, "");
        final VerboseProcess process = new VerboseProcess(
            new ProcessBuilder("cat", "/non-existing-file.txt")
                .redirectErrorStream(true)
        );
        try {
            process.stdout();
            Assertions.fail("exception expected");
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                "should throws an exception",
                ex.getMessage(),
                Matchers.containsString("No such file or directory")
            );
        }
    }

    @Test
    void runsACommandLineScriptWithExceptionNoRedir() throws Exception {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS, "");
        final VerboseProcess process = new VerboseProcess(
            new ProcessBuilder("cat", "/non-existing-file.txt")
        );
        final VerboseProcess.Result result = process.waitFor();
        MatcherAssert.assertThat(
            "should equals 1",
            result.code(),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            "should contains string 'No such file or directory'",
            result.stderr(),
            Matchers.containsString("No such file or directory")
        );
    }

    @Test
    void handlesLongRunningCommand() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS, "");
        final VerboseProcess process = new VerboseProcess(
            new ProcessBuilder("/bin/bash", "-c", "sleep 2; echo 'done'")
        );
        MatcherAssert.assertThat(
            "should starts with 'done'",
            process.stdout(),
            Matchers.startsWith("done")
        );
    }

    @Test
    void rejectsNullProcesses() {
        Assertions.assertThrows(
            RuntimeException.class,
            () -> {
                final ProcessBuilder builder = null;
                new VerboseProcess(builder);
            }
        );
    }

    @Test
    void rejectsStdoutWithLevelAll() {
        try {
            new VerboseProcess(
                Mockito.mock(Process.class), Level.ALL, Level.INFO
            );
            Assertions.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                "should rejects stdout with ALL level",
                ex.getMessage(),
                Matchers.equalTo(
                    StringUtils.join(
                        "stdout LEVEL can't be set to ALL because it is ",
                        "intended only for internal configuration"
                    )
                )
            );
        }
    }

    @Test
    void rejectsStderrWithLevelAll() {
        try {
            new VerboseProcess(
                Mockito.mock(Process.class), Level.INFO, Level.ALL
            );
            Assertions.fail("IllegalArgumentException expected here");
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                "should rejects stderr with ALL level",
                ex.getMessage(),
                Matchers.equalTo(
                    StringUtils.join(
                        "stderr LEVEL can't be set to ALL because it is ",
                        "intended only for internal configuration"
                    )
                )
            );
        }
    }

    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    void quietlyTerminatesLongRunningProcess() throws Exception {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS, "");
        final Process proc = new ProcessBuilder("sleep", "10000").start();
        final VerboseProcess process = new VerboseProcess(proc);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(
            new VerboseRunnable(
                () -> {
                    start.countDown();
                    process.stdoutQuietly();
                    done.countDown();
                }
            )
        ).start();
        start.await();
        TimeUnit.SECONDS.sleep(1L);
        proc.destroy();
        MatcherAssert.assertThat(
            "should be true",
            done.await(1L, TimeUnit.MINUTES),
            Matchers.is(true)
        );
    }

    @Test
    void stdoutQuietlyLogsErrors() {
        final StringWriter writer = new StringWriter();
        org.apache.log4j.Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), writer)
        );
        final ProcessBuilder builder;
        final String message = "hello dear friend";
        if (SystemUtils.IS_OS_WINDOWS) {
            builder = new ProcessBuilder("cmd", "/c", "echo", message, "1>&2");
        } else {
            builder = new ProcessBuilder(
                "cat", String.format("/non-existing-file-%s ", message)
            );
        }
        final VerboseProcess process = new VerboseProcess(
            builder, Level.OFF, Level.WARNING
        );
        process.stdoutQuietly();
        MatcherAssert.assertThat(
            "should quietly logs errors",
            writer.toString(),
            Matchers.containsString(message)
        );
    }

    @Test
    @SuppressWarnings("PMD.AvoidFileStream")
    void logsErrorWhenUnderlyingStreamIsClosed() throws Exception {
        final StringWriter writer = new StringWriter();
        org.apache.log4j.Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), writer)
        );
        final Process prc = Mockito.mock(Process.class);
        final Closeable stdout = new FileInputStream(
            File.createTempFile("temp", "test")
        );
        stdout.close();
        Mockito.doReturn(stdout).when(prc).getInputStream();
        Mockito.doReturn(new ByteArrayInputStream(new byte[0]))
            .when(prc).getErrorStream();
        final VerboseProcess process = new VerboseProcess(
            prc,
            Level.FINEST,
            Level.FINEST
        );
        Logger.debug(
            this,
            "#logsErrorWhenUnderlyingStreamIsClosed(): vrbPrc.hashCode=%s",
            process.hashCode()
        );
        process.stdout();
        MatcherAssert.assertThat(
            "should contains 'Error reading from process stream'",
            writer.toString(),
            Matchers.containsString("Error reading from process stream")
        );
    }

    @Test
    void terminatesMonitorsAndProcessIfClosedInstantly()
        throws Exception {
        this.terminatesMonitorsAndProcessIfClosed(0L);
    }

    @Test
    void terminatesMonitorsAndProcessIfClosedShortly()
        throws Exception {
        this.terminatesMonitorsAndProcessIfClosed(50L);
    }

    @Test
    void terminatesMonitorsAndProcessIfClosedNormal() throws Exception {
        final long delay = 400L;
        this.terminatesMonitorsAndProcessIfClosed(delay);
    }

    /**
     * VerboseProcess can terminate its monitors and underlying Process if
     * closed after specified time since real usage.
     * @param delay Time in milliseconds between usage of vrbcPrc starts and
     *  its close() issued
     * @throws Exception If something goes wrong
     * @checkstyle ExecutableStatementCountCheck (100 lines)
     */
    private void terminatesMonitorsAndProcessIfClosed(final long delay)
        throws Exception {
        final InputStream input = new VerboseProcessTest.InfiniteInputStream('i');
        final InputStream error = new VerboseProcessTest.InfiniteInputStream('e');
        final Process prc = Mockito.mock(Process.class);
        Mockito.doReturn(input).when(prc).getInputStream();
        Mockito.doReturn(error).when(prc).getErrorStream();
        Mockito.doAnswer(
            invocation -> {
                input.close();
                error.close();
                return null;
            }
        ).when(prc).destroy();
        final VerboseProcess process = new VerboseProcess(
            prc,
            Level.FINEST,
            Level.FINEST
        );
        Logger.debug(
            this,
            "terminatesMntrsAndPrcssIfClosed delay=%d vrbPrc.hashCode=%s",
            delay,
            process.hashCode()
        );
        final StringWriter writer = new StringWriter();
        final WriterAppender appender = new WriterAppender(
            new SimpleLayout(),
            writer
        );
        appender.addFilter(new VerboseProcessTest.VrbPrcMonitorFilter(process));
        org.apache.log4j.Logger.getLogger(
            VerboseProcess.class
        ).addAppender(appender);
        if (delay == 0L) {
            process.close();
        } else {
            new Timer(true).schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        process.close();
                    }
                },
                delay
            );
        }
        process.stdoutQuietly();
        TimeUnit.MILLISECONDS.sleep(1000L);
        Mockito.verify(
            prc,
            Mockito.atLeastOnce()
        ).destroy();
        MatcherAssert.assertThat(
            "should not contains 'Error reading from process stream'",
            writer.toString(),
            Matchers.not(Matchers
                .containsString("Error reading from process stream")
            )
        );
    }

    /**
     * {@link InputStream} returning endless flow of characters.
     *
     * @since 0.1
     */
    private final class InfiniteInputStream extends InputStream {
        /**
         * End of line.
         */
        private static final int LINE_FEED = 0xA;

        /**
         * Character, endlessly repeated in the stream.
         */
        private final transient char chr;

        /**
         * Whether the next char in the stream should be EOL.
         */
        private transient boolean feed;

        /**
         * Whether this stream is closed.
         */
        private transient boolean closed;

        /**
         * Construct an InputStream returning endless combination of this
         * character and end of line.
         * @param character Character to return in the stream
         */
        InfiniteInputStream(final char character) {
            super();
            this.chr = character;
        }

        @Override
        public int read() throws IOException {
            if (this.closed) {
                throw new IOException("Stream closed");
            }
            final int next;
            if (this.feed) {
                this.feed = false;
                next = VerboseProcessTest.InfiniteInputStream.LINE_FEED;
            } else {
                this.feed = true;
                next = this.chr;
            }
            return next;
        }

        @Override
        public void close() {
            this.closed = true;
        }
    }

    /**
     * Filter of log messages of {@link VerboseProcess}'s monitor threads.
     *
     * <p>It filters out messages of monitor threads, that doesn't belong to
     * specific {@link VerboseProcess}.
     *
     * @since 0.1
     */
    private final class VrbPrcMonitorFilter extends Filter {
        /**
         * Monitor's log message start.
         */
        private static final String THREADNAME_START = "VrbPrc.Monitor-";

        /**
         * HashCode of {@link VerboseProcess} to filter.
         */
        private final transient int hash;

        /**
         * Create filter for this process.
         *
         * <p>The messages from its monitor threads will be filtered in.
         * @param prc Process
         */
        VrbPrcMonitorFilter(final VerboseProcess prc) {
            super();
            this.hash = prc.hashCode();
        }

        @Override
        public int decide(final LoggingEvent event) {
            final String thread = event.getThreadName();
            final int decision;
            if (thread.startsWith(VerboseProcessTest.VrbPrcMonitorFilter.THREADNAME_START
                + this.hash
            )) {
                decision = Filter.ACCEPT;
            } else {
                decision = Filter.DENY;
            }
            return decision;
        }
    }
}

/**
 * Copyright (c) 2012-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.log;

import java.io.ByteArrayInputStream;
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
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test case for {@link VerboseProcess}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (200 lines)
 * @checkstyle ClassDataAbstractionCoupling (200 lines)
 * @todo #18 Locale/encoding problem in two test methods here. I'm not
 *  sure how to fix them, but they should be fixed. They fail on some
 *  machines, while run perfectly on others. They also fail when being
 *  executed from IntelliJ.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class VerboseProcessTest {

    /**
     * VerboseProcess can run a command line script.
     * @throws Exception If something goes wrong
     * @link http://stackoverflow.com/questions/24802042
     */
    @Test
    @Ignore
    public void runsACommandLineScript() throws Exception {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        final VerboseProcess process = new VerboseProcess(
            new ProcessBuilder("echo", "hey \u20ac!").redirectErrorStream(true)
        );
        MatcherAssert.assertThat(
            process.stdout(),
            Matchers.containsString("\u20ac!")
        );
    }

    /**
     * VerboseProcess can run a command line script.
     * @throws Exception If something goes wrong
     * @link http://stackoverflow.com/questions/24802042
     */
    @Test
    @Ignore
    public void echosUnicodeCorrectly() throws Exception {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        MatcherAssert.assertThat(
            new VerboseProcess(
                new ProcessBuilder(
                    "/bin/bash", "-c",
                    "echo -n \u0442\u0435\u0441\u0442 | hexdump"
                )
            ).stdout(),
            Matchers.containsString("0000000 d1 82 d0 b5 d1 81 d1 82")
        );
    }

    /**
     * VerboseProcess can run a command line script with exception.
     * @throws Exception If something goes wrong
     */
    @Test
    public void runsACommandLineScriptWithException() throws Exception {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        final VerboseProcess process = new VerboseProcess(
            new ProcessBuilder("cat", "/non-existing-file.txt")
                .redirectErrorStream(true)
        );
        try {
            process.stdout();
            Assert.fail("exception expected");
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString("No such file or directory")
            );
        }
    }

    /**
     * VerboseProcess can handle a long running command.
     * @throws Exception If something goes wrong
     */
    @Test
    public void handlesLongRunningCommand() throws Exception {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        final VerboseProcess process = new VerboseProcess(
            new ProcessBuilder("/bin/bash", "-c", "sleep 2; echo 'done'")
        );
        MatcherAssert.assertThat(
            process.stdout(),
            Matchers.startsWith("done")
        );
    }

    /**
     * VerboseProcess can reject NULL.
     * @throws Exception If something goes wrong
     */
    @Test(expected = RuntimeException.class)
    public void rejectsNullProcesses() throws Exception {
        final ProcessBuilder builder = null;
        new VerboseProcess(builder);
    }

    /**
     * VerboseProcess can quietly terminate a long-running process.
     * @throws Exception If something goes wrong
     */
    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void quietlyTerminatesLongRunningProcess() throws Exception {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        final Process proc = new ProcessBuilder("sleep", "10000").start();
        final VerboseProcess process = new VerboseProcess(proc);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(
            new VerboseRunnable(
                new Runnable() {
                    @Override
                    public void run() {
                        start.countDown();
                        process.stdoutQuietly();
                        done.countDown();
                    }
                }
            )
        ).start();
        start.await();
        TimeUnit.SECONDS.sleep(1L);
        proc.destroy();
        MatcherAssert.assertThat(
            done.await(1L, TimeUnit.MINUTES),
            Matchers.is(true)
        );
    }

    /**
     * VerboseProcess.stdoutQuietly() should log stderr messages.
     * @throws Exception If something goes wrong
     */
    @Test
    public void stdoutQuietlyLogsErrors() throws Exception {
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
            builder, Level.OFF, Level.ALL
        );
        process.stdoutQuietly();
        MatcherAssert.assertThat(
            writer.toString(),
            Matchers.containsString(message)
        );
    }

    /**
     * VerboseProcess exits "gracefully" when it can't read from the process
     * stream, and logs the error that is thrown.
     * @throws Exception If something goes wrong
     */
    @Test
    public void logsErrorWhenUnderlyingStreamIsClosed() throws Exception {
        final StringWriter writer = new StringWriter();
        org.apache.log4j.Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), writer)
        );
        final Process prc = Mockito.mock(Process.class);
        final InputStream stdout = new FileInputStream(
            File.createTempFile("temp", "test")
        );
        stdout.close();
        Mockito.doReturn(stdout).when(prc).getInputStream();
        Mockito.doReturn(new ByteArrayInputStream(new byte[0]))
            .when(prc).getErrorStream();
        new VerboseProcess(prc, Level.ALL, Level.ALL).stdout();
        MatcherAssert.assertThat(
            writer.toString(),
            Matchers.containsString("Error reading from process stream")
        );
    }

    /**
     * VerboseProcess can terminate its monitors and underlying Process when
     * closed.
     * @throws Exception If something goes wrong
     */
    @Test
    public void terminatesMonitorsAndUnderlyingProcessWhenClosed()
        throws Exception {
        final InputStream inputStream = new InfiniteInputStream('i');
        final InputStream errorStream = new InfiniteInputStream('e');
        final Process prc = Mockito.mock(Process.class);
        Mockito.doReturn(inputStream).when(prc).getInputStream();
        Mockito.doReturn(errorStream).when(prc).getErrorStream();
        Mockito.doAnswer(
            new Answer<Void>() {
                @Override
                public Void answer(final InvocationOnMock invocation)
                    throws Exception {
                    inputStream.close();
                    errorStream.close();
                    return null;
                }
            }
        ).when(prc).destroy();
        final VerboseProcess verboseProcess = new VerboseProcess(
            prc,
            Level.FINEST,
            Level.FINEST
        );
        final StringWriter writer = new StringWriter();
        final WriterAppender appender = new WriterAppender(
            new SimpleLayout(),
            writer
        );
        appender.addFilter(new VrbPrcMonitorFilter(verboseProcess));
        org.apache.log4j.Logger.getLogger(
            VerboseProcess.class
        ).addAppender(appender);
        final int readInterval = 50;
        new Timer(true).schedule(
            new TimerTask() {
                @Override
                public void run() {
                    verboseProcess.close();
                }
            },
            readInterval
        );
        verboseProcess.stdoutQuietly();
        final int monitorsInterval = 500;
        TimeUnit.MILLISECONDS.sleep(monitorsInterval);
        Mockito.verify(
            prc,
            Mockito.atLeastOnce()
        ).destroy();
        MatcherAssert.assertThat(
            writer.toString(),
            Matchers.not(Matchers
                    .containsString("Error reading from process stream")
            )
        );
    }

    /**
     * {@link InputStream} returning endless flow of characters.
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
        public InfiniteInputStream(final char character) {
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
                next = LINE_FEED;
            } else {
                this.feed = true;
                next = this.chr;
            }
            return next;
        }

        @Override
        public void close() throws IOException {
            this.closed = true;
        }
    }

    /**
     * Filter of log messages of {@link VerboseProcess}'s monitor threads.<br/>
     * It filters out messages of monitor threads, that doesn't belong to
     * specific {@link VerboseProcess}.
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
         * Create filter for this process.<br/>
         * The messages from its monitor threads will be filtered in.
         * @param prc Process
         */
        public VrbPrcMonitorFilter(final VerboseProcess prc) {
            super();
            this.hash = prc.hashCode();
        }

        @Override
        public int decide(final LoggingEvent event) {
            final String thread = event.getThreadName();
            final int decision;
            if (thread.startsWith(THREADNAME_START)) {
                if (thread.equals(THREADNAME_START + this.hash)) {
                    decision = Filter.ACCEPT;
                } else {
                    decision = Filter.DENY;
                }
            } else {
                decision = Filter.NEUTRAL;
            }
            return decision;
        }

    }
}

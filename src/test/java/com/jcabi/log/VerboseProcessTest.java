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
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

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
     * @throws Exception
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
            Matchers.containsString("underlying process stream was closed")
        );
    }

}
;
/**
 * Copyright (c) 2012-2013, JCabi.com
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.CharEncoding;

/**
 * Utility class for getting {@code stdout} from a running process
 * and logging it through SLF4J.
 *
 * <p>For example:
 *
 * <pre> String name = new VerboseProcess(
 *   new ProcessBuilder("who", "am", "i")
 * ).stdout();</pre>
 *
 * <p>The class throws an exception if the process returns a non-zero exit
 * code.
 *
 * <p>The class is thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@ToString
@EqualsAndHashCode(of = "process")
public final class VerboseProcess {

    /**
     * The process we're working with.
     */
    private final transient Process process;

    /**
     * Public ctor.
     * @param prc The process to work with
     */
    public VerboseProcess(@NotNull final Process prc) {
        this.process = prc;
    }

    /**
     * Public ctor (builder will be configured to redirect error stream to
     * the {@code stdout} and will receive an empty {@code stdin}).
     * @param builder Process builder to work with
     */
    public VerboseProcess(@NotNull final ProcessBuilder builder) {
        builder.redirectErrorStream(true);
        try {
            this.process = builder.start();
            this.process.getOutputStream().close();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Get {@code stdout} from the process, after its finish (the method will
     * wait for the process and log its output).
     *
     * <p>The method will check process exit code, and if it won't be equal
     * to zero a runtime exception will be thrown. A non-zero exit code
     * usually is an indicator of problem. If you want to ignore this code,
     * use {@link #stdoutQuietly()} instead.
     *
     * @return Full {@code stdout} of the process
     */
    public String stdout() {
        return this.stdout(true);
    }

    /**
     * Get {@code stdout} from the process, after its finish (the method will
     * wait for the process and log its output).
     *
     * <p>This method ignores exit code of the process. Even if it is
     * not equal to zero (which usually is an indicator of an error), the
     * method will quietly return its output. The method is useful when
     * you're running a background process. You will kill it with
     * {@link Process#destroy()}, which usually will lead to a non-zero
     * exit code, which you want to ignore.
     *
     * @return Full {@code stdout} of the process
     * @since 0.10
     */
    public String stdoutQuietly() {
        return this.stdout(false);
    }

    /**
     * Get standard output and check for non-zero exit code (if required).
     * @param check TRUE if we should check for non-zero exit code
     * @return Full {@code stdout} of the process
     */
    private String stdout(final boolean check) {
        final long start = System.currentTimeMillis();
        final String stdout;
        try {
            stdout = this.waitFor();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        final int code = this.process.exitValue();
        Logger.debug(
            this,
            "#stdout(): process %s completed (code=%d, size=%d) in %[ms]s",
            this.process, code, stdout.length(),
            System.currentTimeMillis() - start
        );
        if (check && code != 0) {
            throw new IllegalArgumentException(
                Logger.format("Non-zero exit code %d: %[text]s", code, stdout)
            );
        }
        return stdout;
    }

    /**
     * Wait for the process to stop, logging its output in parallel.
     * @return Stdout produced by the process
     * @throws InterruptedException If interrupted in between
     */
    private String waitFor() throws InterruptedException {
        final CountDownLatch done = new CountDownLatch(2);
        final StringBuffer stdout = new StringBuffer(0);
        final StringBuffer stderr = new StringBuffer(0);
        Logger.debug(
            this,
            "#waitFor(): waiting for stdout of %s in %s...",
            this.process,
            this.monitor(
                this.process.getInputStream(),
                done, stdout, Level.INFO
            )
        );
        Logger.debug(
            this,
            "#waitFor(): waiting for stderr of %s in %s...",
            this.process,
            this.monitor(
                this.process.getErrorStream(),
                done, stderr, Level.WARNING
            )
        );
        try {
            this.process.waitFor();
        } finally {
            Logger.debug(this, "#waitFor(): process finished", this.process);
            done.await(2L, TimeUnit.SECONDS);
        }
        return stdout.toString();
    }

    /**
     * Monitor this input stream.
     * @param stream Stream to monitor
     * @param done Count down latch to signal when done
     * @param buffer Buffer to write to
     * @param level Logging level
     * @return Thread which is monitoring
     * @checkstyle ParameterNumber (5 lines)
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private Thread monitor(final InputStream stream, final CountDownLatch done,
        final StringBuffer buffer, final Level level) {
        final Thread thread = new Thread(
            new VerboseRunnable(
                // @checkstyle AnonInnerLength (100 lines)
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                stream,
                                Charset.forName(CharEncoding.UTF_8)
                            )
                        );
                        try {
                            while (true) {
                                final String line = reader.readLine();
                                if (line == null) {
                                    break;
                                }
                                Logger.log(
                                    level, VerboseProcess.class,
                                    ">> %s", line
                                );
                                buffer.append(line);
                            }
                            done.countDown();
                        } finally {
                            try {
                                reader.close();
                            } catch (IOException ex) {
                                Logger.error(
                                    this,
                                    "failed to close reader: %[exception]s", ex
                                );
                            }
                        }
                        return null;
                    }
                },
                false
            )
        );
        thread.setName("VerboseProcess");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

}

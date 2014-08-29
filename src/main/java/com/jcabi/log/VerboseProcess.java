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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
     * Charset.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * The process we're working with.
     */
    private final transient Process process;

    /**
     * Log level for stdout.
     */
    private final transient Level olevel;

    /**
     * Log level for stderr.
     */
    private final transient Level elevel;

    /**
     * Public ctor.
     * @param prc The process to work with
     */
    public VerboseProcess(final Process prc) {
        this(prc, Level.INFO, Level.WARNING);
    }

    /**
     * Public ctor (builder will be configured to redirect error input to
     * the {@code stdout} and will receive an empty {@code stdin}).
     * @param builder Process builder to work with
     */
    public VerboseProcess(final ProcessBuilder builder) {
        this(VerboseProcess.start(builder));
    }

    /**
     * Public ctor, with a given process and logging levels for {@code stdout}
     * and {@code stderr}.
     * @param prc Process to execute and monitor
     * @param stdout Log level for stdout
     * @param stderr Log level for stderr
     * @since 0.11
     */
    public VerboseProcess(final Process prc, final Level stdout,
        final Level stderr) {
        if (prc == null) {
            throw new IllegalArgumentException("process can't be NULL");
        }
        if (stdout == null) {
            throw new IllegalArgumentException("stdout LEVEL can't be NULL");
        }
        if (stderr == null) {
            throw new IllegalArgumentException("stderr LEVEL can't be NULL");
        }
        this.process = prc;
        this.olevel = stdout;
        this.elevel = stderr;
    }

    /**
     * Public ctor, with a given process and logging levels for {@code stdout}
     * and {@code stderr}.
     * @param bdr Process builder to execute and monitor
     * @param stdout Log level for stdout
     * @param stderr Log level for stderr
     * @since 0.12
     */
    public VerboseProcess(final ProcessBuilder bdr, final Level stdout,
        final Level stderr) {
        this(VerboseProcess.start(bdr), stdout, stderr);
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
     * Start a process from the given builder.
     * @param builder Process builder to work with
     * @return Process started
     */
    private static Process start(final ProcessBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can't be NULL");
        }
        try {
            final Process process = builder.start();
            process.getOutputStream().close();
            return process;
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Get standard output and check for non-zero exit code (if required).
     * @param check TRUE if we should check for non-zero exit code
     * @return Full {@code stdout} of the process
     */
    @SuppressWarnings("PMD.PrematureDeclaration")
    private String stdout(final boolean check) {
        final long start = System.currentTimeMillis();
        final String stdout;
        try {
            stdout = this.waitFor();
        } catch (final InterruptedException ex) {
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
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        Logger.debug(
            this,
            "#waitFor(): waiting for stdout of %s in %s...",
            this.process,
            VerboseProcess.monitor(
                this.process.getInputStream(),
                done, stdout, this.olevel
            )
        );
        Logger.debug(
            this,
            "#waitFor(): waiting for stderr of %s in %s...",
            this.process,
            VerboseProcess.monitor(
                this.process.getErrorStream(),
                done, new ByteArrayOutputStream(), this.elevel
            )
        );
        try {
            this.process.waitFor();
        } finally {
            Logger.debug(
                this, "#waitFor(): process finished: %s", this.process
            );
            if (!done.await(2L, TimeUnit.SECONDS)) {
                Logger.error(this, "#wait() failed");
            }
        }
        try {
            return stdout.toString(VerboseProcess.UTF_8);
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Monitor this input input.
     * @param input Stream to monitor
     * @param done Count down latch to signal when done
     * @param output Buffer to write to
     * @param level Logging level
     * @return Thread which is monitoring
     * @checkstyle ParameterNumber (6 lines)
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private static Thread monitor(final InputStream input,
        final CountDownLatch done,
        final OutputStream output, final Level level) {
        final Thread thread = new Thread(
            new VerboseRunnable(
                new VerboseProcess.Monitor(input, done, output, level),
                false
            )
        );
        thread.setName("VerboseProcess");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * Close quietly.
     * @param res Resource to close
     */
    private static void close(final Closeable res) {
        try {
            res.close();
        } catch (final IOException ex) {
            Logger.error(
                VerboseProcess.class,
                "failed to close resource: %[exception]s",
                ex
            );
        }
    }

    /**
     * Stream monitor.
     */
    private static final class Monitor implements Callable<Void> {
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
         * @checkstyle ParameterNumber (5 lines)
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
            final BufferedReader reader = new BufferedReader(
                new InputStreamReader(this.input, VerboseProcess.UTF_8)
            );
            try {
                final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(this.output, VerboseProcess.UTF_8)
                );
                try {
                    while (true) {
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
                } finally {
                    VerboseProcess.close(writer);
                    this.done.countDown();
                }
            } finally {
                VerboseProcess.close(reader);
            }
            return null;
        }
    }

}

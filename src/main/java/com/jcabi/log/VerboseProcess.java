/*
 * Copyright (c) 2012-2025, jcabi.com
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
 * @since 0.5
 */
@SuppressWarnings({ "PMD.DoNotUseThreads", "PMD.TooManyMethods" })
public final class VerboseProcess implements Closeable {

    /**
     * Charset.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * Number of stream monitors.
     */
    private static final int N_MONITORS = 2;

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
     * Stream monitors.
     */
    private final transient Thread[] monitors;

    /**
     * Flag to indicate the closure of this process.
     */
    private transient boolean closed;

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
     * and {@code stderr}. Neither {@code stdout} nor {@code stderr} cannot be
     * set to {@link Level#ALL} because it is intended to be used only for
     * internal configuration.
     * @param prc Process to execute and monitor
     * @param stdout Log level for stdout
     * @param stderr Log level for stderr
     * @since 0.11
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
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
        if (Level.ALL.equals(stdout)) {
            throw new IllegalArgumentException(
                "stdout LEVEL can't be set to ALL because it is intended only for internal configuration"
            );
        }
        if (Level.ALL.equals(stderr)) {
            throw new IllegalArgumentException(
                "stderr LEVEL can't be set to ALL because it is intended only for internal configuration"
            );
        }
        this.process = prc;
        this.olevel = stdout;
        this.elevel = stderr;
        this.monitors = new Thread[VerboseProcess.N_MONITORS];
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
     * Wait for the process to stop, logging its output in parallel.
     * @return Stdout produced by the process
     * @throws InterruptedException If interrupted in between
     */
    public VerboseProcess.Result waitFor() throws InterruptedException {
        final CountDownLatch done = new CountDownLatch(
            VerboseProcess.N_MONITORS
        );
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        this.launchMonitors(done, stdout, stderr);
        final int code;
        try {
            code = this.process.waitFor();
        } finally {
            Logger.debug(
                this,
                "#waitFor(): process finished: %s",
                this.process
            );
            if (!done.await(2L, TimeUnit.SECONDS)) {
                Logger.error(this, "#wait() failed");
            }
        }
        try {
            return new VerboseProcess.Result(
                code,
                stdout.toString(VerboseProcess.UTF_8),
                stderr.toString(VerboseProcess.UTF_8)
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void close() {
        synchronized (this.monitors) {
            this.closed = true;
        }
        for (final Thread monitor : this.monitors) {
            if (monitor != null) {
                monitor.interrupt();
                Logger.debug(this, "Monitor interrupted");
            }
        }
        this.process.destroy();
        Logger.debug(this, "Underlying process destroyed");
    }

    /**
     * Start a process from the given builder.
     * @param builder Process builder to work with
     * @return Process started
     */
    private static Process start(final ProcessBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Builder can't be NULL");
        }
        Logger.debug(
            VerboseProcess.class,
            "#start(): %s",
            String.join(" ", builder.command())
        );
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
        final VerboseProcess.Result result;
        try {
            result = this.waitFor();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        Logger.debug(
            this,
            "#stdout(): process %s completed (code=%d, size=%d) in %[ms]s",
            this.process, result.code(), result.stdout().length(),
            System.currentTimeMillis() - start
        );
        if (check && result.code() != 0) {
            throw new IllegalArgumentException(
                Logger.format(
                    "Non-zero exit code %d: %[text]s",
                    result.code(),
                    result.stdout()
                )
            );
        }
        return result.stdout();
    }

    /**
     * Launch monitors for the underlying process.
     * @param done Latch that signals termination of all monitors
     * @param stdout Stream to write the underlying process's output
     * @param stderr Stream to wrint the underlying process's error output
     */
    private void launchMonitors(
        final CountDownLatch done,
        final ByteArrayOutputStream stdout,
        final ByteArrayOutputStream stderr) {
        synchronized (this.monitors) {
            if (this.closed) {
                done.countDown();
                done.countDown();
            } else {
                this.monitors[0] = this.monitor(
                    this.process.getInputStream(),
                    done,
                    stdout,
                    this.olevel,
                    "out"
                );
                Logger.debug(
                    this,
                    "#waitFor(): waiting for stdout of %s in %s...",
                    this.process,
                    this.monitors[0]
                );
                this.monitors[1] = this.monitor(
                    this.process.getErrorStream(),
                    done,
                    stderr,
                    this.elevel,
                    "err"
                );
                Logger.debug(
                    this,
                    "#waitFor(): waiting for stderr of %s in %s...",
                    this.process,
                    this.monitors[1]
                );
            }
        }
    }

    /**
     * Monitor this input input.
     * @param input Stream to monitor
     * @param done Count down latch to signal when done
     * @param output Buffer to write to
     * @param level Logging level
     * @param name Name of this monitor. Used in logging as part of threadname
     * @return Thread which is monitoring
     * @checkstyle ParameterNumber (6 lines)
     */
    private Thread monitor(final InputStream input,
        final CountDownLatch done,
        final OutputStream output, final Level level, final String name) {
        final Thread thread = new Thread(
            new VerboseRunnable(
                new VerboseProcess.Monitor(input, done, output, level),
                false
            )
        );
        thread.setName(
            String.format(
                "VrbPrc.Monitor-%d-%s",
                this.hashCode(),
                name
            )
        );
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * Stream monitor.
     *
     * @since 0.1
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
            try (BufferedReader reader = new BufferedReader(
                Channels.newReader(
                    Channels.newChannel(this.input),
                    VerboseProcess.UTF_8
                ));
                BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(this.output, VerboseProcess.UTF_8)
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

    /**
     * Class representing the result of a process.
     *
     * @since 0.1
     */
    public static final class Result {

        /**
         * Returned code from the process.
         */
        private final transient int exit;

        /**
         * {@code stdout} from the process.
         */
        private final transient String out;

        /**
         * {@code stderr} from the process.
         */
        private final transient String err;

        /**
         * Result class constructor.
         * @param code The exit code.
         * @param stdout The {@code stdout} from the process.
         * @param stderr The {@code stderr} from the process.
         */
        Result(final int code, final String stdout, final String stderr) {
            this.exit = code;
            this.out = stdout;
            this.err = stderr;
        }

        /**
         * Get {@code code} from the process.
         * @return Full {@code code} of the process
         */
        public int code() {
            return this.exit;
        }

        /**
         * Get {@code stdout} from the process.
         * @return Full {@code stdout} of the process
         */
        public String stdout() {
            return this.out;
        }

        /**
         * Get {@code stderr} from the process.
         * @return Full {@code stderr} of the process
         */
        public String stderr() {
            return this.err;
        }
    }
}

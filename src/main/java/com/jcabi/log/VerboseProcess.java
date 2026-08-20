/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public final class VerboseProcess implements Closeable {

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
     * Public ctor, with a given process and logging levels for {@code stdout}
     * and {@code stderr}. Neither {@code stdout} nor {@code stderr} cannot be
     * set to {@link Level#ALL} because it is intended to be used only for
     * internal configuration.
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
        // @checkstyle ConstructorsCodeFreeCheck (11 lines)
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
    public Result waitFor() throws InterruptedException {
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
        return new Result(
            code,
            stdout.toString(StandardCharsets.UTF_8),
            stderr.toString(StandardCharsets.UTF_8)
        );
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

    private String stdout(final boolean check) {
        final long start = System.currentTimeMillis();
        final Result result;
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

    private Thread monitor(final InputStream input,
        final CountDownLatch done,
        final OutputStream output, final Level level, final String name) {
        final Thread thread = new Thread(
            new VerboseRunnable(
                new Monitor(input, done, output, level),
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
}

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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper of {@link Callable}, that logs all uncaught runtime exceptions.
 *
 * <p>You can use it with scheduled executor, for example:
 *
 * <pre> Executors.newFixedThreadPool(1).submit(
 *   new VerboseCallable(callable, true)
 * );</pre>
 *
 * <p>Now, every runtime exception that is not caught inside your
 * {@link Callable} will be reported to log (using {@link Logger}).
 * Two-arguments constructor can be used when you need to instruct the class
 * about what to do with the exception: either swallow it or escalate.
 * Sometimes it's very important to swallow exceptions. Otherwise an entire
 * thread may get stuck (like in the example above).
 *
 * <p>This class is thread-safe.
 *
 * @param <T> Type of result
 * @see VerboseThreads
 * @since 0.16
 * @link <a href="http://www.ibm.com/developerworks/java/library/j-jtp05236/index.html">Java theory and practice: Dealing with InterruptedException</a>
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public final class VerboseCallable<T> implements Callable<T> {

    /**
     * Original runnable.
     */
    private final transient Callable<T> origin;

    /**
     * Rethrow exceptions (TRUE) or swallow them?
     */
    private final transient boolean rethrow;

    /**
     * Shall we report a full stacktrace?
     */
    private final transient boolean verbose;

    /**
     * Default constructor, doesn't swallow exceptions.
     * @param callable Callable to wrap
     */
    public VerboseCallable(final Callable<T> callable) {
        this(callable, false);
    }

    /**
     * Default constructor, doesn't swallow exceptions.
     * @param callable Callable to wrap
     * @param swallow Shall we swallow exceptions
     *  ({@code TRUE}) or re-throw
     *  ({@code FALSE})? Exception swallowing means that {@link #call()}
     *  will never throw any exceptions (in any case all exceptions are logged
     *  using {@link Logger}.
     */
    public VerboseCallable(final Callable<T> callable, final boolean swallow) {
        this(callable, swallow, true);
    }

    /**
     * Default constructor.
     * @param runnable Runnable to wrap
     * @param swallow Shall we swallow exceptions
     *  ({@code TRUE}) or re-throw
     *  ({@code FALSE})? Exception swallowing means that {@link #call()}
     *  will never throw any exceptions (in any case all exceptions are logged
     *  using {@link Logger}.
     * @param vrbs Shall we report the entire
     *  stacktrace of the exception
     *  ({@code TRUE}) or just its message in one line ({@code FALSE})
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public VerboseCallable(final Runnable runnable,
        final boolean swallow, final boolean vrbs) {
        this(
            new Callable<T>() {
                @Override
                public T call() {
                    runnable.run();
                    return null;
                }

                @Override
                public String toString() {
                    return runnable.toString();
                }
            },
            swallow,
            vrbs
        );
    }

    /**
     * Default constructor, with configurable behavior for exceptions.
     * @param runnable Runnable to wrap
     * @param swallow Shall we swallow exceptions
     *  ({@code TRUE}) or re-throw
     *  ({@code FALSE})? Exception swallowing means that {@link #call()}
     *  will never throw any exceptions (in any case all exceptions are logged
     *  using {@link Logger}.
     */
    public VerboseCallable(final Runnable runnable, final boolean swallow) {
        this(runnable, swallow, true);
    }

    /**
     * Default constructor, with fully configurable behavior.
     * @param callable Runnable to wrap
     * @param swallow Shall we swallow exceptions
     *  ({@code TRUE}) or re-throw
     *  ({@code FALSE})? Exception swallowing means that {@link #call()}
     *  will never throw any exceptions (in any case all exceptions are logged
     *  using {@link Logger}.
     * @param vrbs Shall we report the entire
     *  stacktrace of the exception
     *  ({@code TRUE}) or just its message in one line ({@code FALSE})
     */
    @SuppressWarnings("PMD.BooleanInversion")
    public VerboseCallable(final Callable<T> callable,
        final boolean swallow, final boolean vrbs) {
        this.origin = callable;
        this.rethrow = !swallow;
        this.verbose = vrbs;
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public T call() throws Exception {
        T result = null;
        try {
            result = this.origin.call();
            // @checkstyle IllegalCatch (1 line)
        } catch (final RuntimeException ex) {
            if (this.rethrow) {
                Logger.warn(
                    this, "Escalated runtime exception: %s", this.tail(ex)
                );
                throw ex;
            }
            Logger.warn(this, "Swallowed runtime exception: %s", this.tail(ex));
            // @checkstyle IllegalCatch (1 line)
        } catch (final Exception ex) {
            if (this.rethrow) {
                Logger.warn(this, "Escalated exception: %s", this.tail(ex));
                throw ex;
            }
            Logger.warn(this, "Swallowed exception: %s", this.tail(ex));
            // @checkstyle IllegalCatch (1 line)
        } catch (final Error error) {
            if (this.rethrow) {
                Logger.error(this, "Escalated error: %s", this.tail(error));
                throw error;
            }
            Logger.error(this, "Swallowed error: %s", this.tail(error));
        }
        try {
            TimeUnit.MICROSECONDS.sleep(1L);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        return result;
    }

    /**
     * Make a tail of the error/warning message, using the exception thrown.
     * @param throwable The exception/error caught
     * @return The message to show in logs
     */
    private String tail(final Throwable throwable) {
        final String tail;
        if (this.verbose) {
            tail = Logger.format("%[exception]s", throwable);
        } else {
            tail = Logger.format(
                "%[type]s('%s')",
                throwable,
                throwable.getMessage()
            );
        }
        return tail;
    }

}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link VerboseRunnable}.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
final class VerboseRunnableTest {

    @Test
    void logsExceptionsInRunnable() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new VerboseRunnable(
                (Runnable) () -> {
                    throw new IllegalArgumentException("oops");
                }
            ).run()
        );
    }

    @Test
    void swallowsExceptionsInRunnable() {
        new VerboseRunnable(
            (Runnable) () -> {
                throw new IllegalArgumentException("boom");
            },
            true
        ).run();
    }

    @Test
    void swallowsExceptionsInCallable() {
        new VerboseRunnable(
            () -> {
                throw new IllegalArgumentException("boom-2");
            },
            true
        ).run();
    }

    @Test
    void translatesToStringFromUnderlyingRunnable() {
        final String text = "some text abc";
        final Runnable verbose = new VerboseRunnable(
            new Runnable() {
                @Override
                public void run() {
                    assert true;
                }

                @Override
                public String toString() {
                    return text;
                }
            }
        );
        MatcherAssert.assertThat(
            "should has 'some text abc'",
            verbose,
            Matchers.hasToString(Matchers.containsString(text))
        );
    }

    @Test
    void translatesToStringFromUnderlyingCallable() {
        final String text = "some text abc-2";
        final Runnable verbose = new VerboseRunnable(
            new Callable<Void>() {
                @Override
                public Void call() {
                    return null;
                }

                @Override
                public String toString() {
                    return text;
                }
            },
            true
        );
        MatcherAssert.assertThat(
            "should has 'some text abc-2'",
            verbose,
            Matchers.hasToString(Matchers.containsString(text))
        );
    }

    @Test
    void preservesInterruptedStatus() throws Exception {
        final ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
        try {
            final AtomicReference<Thread> thread = new AtomicReference<>();
            final AtomicInteger runs = new AtomicInteger();
            svc.scheduleWithFixedDelay(
                new VerboseRunnable(
                    () -> {
                        runs.addAndGet(1);
                        thread.set(Thread.currentThread());
                        TimeUnit.HOURS.sleep(1L);
                        return null;
                    },
                    true,
                    false
                ),
                1L, 1L,
                TimeUnit.MICROSECONDS
            );
            while (thread.get() == null) {
                TimeUnit.MILLISECONDS.sleep(1L);
            }
            thread.get().interrupt();
            TimeUnit.SECONDS.sleep(1L);
            svc.shutdown();
            MatcherAssert.assertThat("should be 1", runs.get(), Matchers.is(1));
            MatcherAssert.assertThat(
                "should be true",
                svc.awaitTermination(1L, TimeUnit.SECONDS),
                Matchers.is(true)
            );
        } finally {
            svc.shutdown();
            try {
                if (!svc.awaitTermination(1, TimeUnit.SECONDS)) {
                    svc.shutdownNow();
                }
            } catch (final InterruptedException ex) {
                svc.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}

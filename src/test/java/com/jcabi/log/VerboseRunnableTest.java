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
@SuppressWarnings({ "PMD.DoNotUseThreads", "PMD.TooManyMethods", "PMD.CloseResource" })
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
            "should contains 'some text abc'",
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
            "should contains 'some text abc-2'",
            verbose,
            Matchers.hasToString(Matchers.containsString(text))
        );
    }

    @Test
    void preservesInterruptedStatus() throws Exception {
        final ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
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
    }

}

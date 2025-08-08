/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link VerboseThreads}.
 * @since 0.1
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.CloseResource"})
final class VerboseThreadsTest {

    @Test
    void instantiatesThreadsOnDemand() throws Exception {
        final ExecutorService service = Executors
            .newSingleThreadExecutor(new VerboseThreads("foo"));
        service.execute(
            () -> {
                throw new IllegalArgumentException("oops");
            }
        );
        TimeUnit.SECONDS.sleep(1L);
        service.shutdown();
    }

    @Test
    void logsWhenThreadsAreNotDying() throws Exception {
        final ExecutorService service = Executors
            .newSingleThreadExecutor(new VerboseThreads(this));
        final Future<?> future = service.submit(
            (Runnable) () -> {
                throw new IllegalArgumentException("boom");
            }
        );
        while (!future.isDone()) {
            TimeUnit.SECONDS.sleep(1L);
        }
        service.shutdown();
    }

}

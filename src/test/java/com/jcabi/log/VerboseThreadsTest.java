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
final class VerboseThreadsTest {

    @Test
    void instantiatesThreadsOnDemand() throws Exception {
        final ExecutorService svc = Executors
                .newSingleThreadExecutor(new VerboseThreads("foo"));
        try {
            svc.execute(
                () -> {
                    throw new IllegalArgumentException("oops");
                }
            );
            TimeUnit.SECONDS.sleep(1L);
        } finally {
            svc.shutdown();
            try {
                if (!svc.awaitTermination(1, TimeUnit.SECONDS)) {
                    svc.shutdownNow();
                }
            } catch (InterruptedException e) {
                svc.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    void logsWhenThreadsAreNotDying() throws Exception {
        try (ExecutorService service = Executors
            .newSingleThreadExecutor(new VerboseThreads(this))) {
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

}

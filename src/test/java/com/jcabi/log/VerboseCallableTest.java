/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link VerboseCallable}.
 *
 * @since 0.16
 */
final class VerboseCallableTest {

    /**
     * VerboseCallable can log exceptions inside Callable.
     */
    @Test
    void logsExceptionsInCallable() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new VerboseCallable<Integer>(
                () -> {
                    throw new IllegalArgumentException("oops");
                }
            ).call()
        );
    }

    /**
     * VerboseCallable keeps the result when the thread is interrupted.
     *
     * @throws Exception If fails
     */
    @Test
    void keepsResultWhenThreadInterrupted() throws Exception {
        Thread.currentThread().interrupt();
        try {
            MatcherAssert.assertThat(
                "interrupted thread must not discard a good result",
                new VerboseCallable<Integer>(() -> 1, true).call(),
                Matchers.equalTo(1)
            );
        } finally {
            Thread.interrupted();
        }
    }
}

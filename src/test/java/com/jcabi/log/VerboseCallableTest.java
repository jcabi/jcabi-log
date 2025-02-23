/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link VerboseCallable}.
 * @since 0.16
 */
@SuppressWarnings({ "PMD.DoNotUseThreads", "PMD.TooManyMethods" })
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

}

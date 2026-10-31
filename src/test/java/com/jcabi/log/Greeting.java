/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.concurrent.Callable;

/**
 * Task that logs one plain greeting through {@link Logger}.
 *
 * @since 0.24.4
 */
public final class Greeting implements Callable<String> {

    /**
     * Ctor.
     */
    public Greeting() {
        // Nothing to initialize
    }

    @Override
    public String call() {
        Logger.info(this, "Hello, %s!", "world");
        return "logged";
    }
}

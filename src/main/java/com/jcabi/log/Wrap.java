/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Runnable decorator.
 * @since 0.1
 */
final class Wrap implements Runnable {

    /**
     * Origin runnable.
     */
    private final transient Runnable origin;

    /**
     * Ctor.
     * @param runnable Origin runnable
     */
    Wrap(final Runnable runnable) {
        this.origin = runnable;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void run() {
        try {
            this.origin.run();
            // @checkstyle IllegalCatch (1 line)
        } catch (final RuntimeException ex) {
            Logger.warn(
                this,
                "%s: %[exception]s",
                Thread.currentThread().getName(),
                ex
            );
            throw ex;
            // @checkstyle IllegalCatch (1 line)
        } catch (final Error error) {
            Logger.error(
                this,
                "%s (error): %[exception]s",
                Thread.currentThread().getName(),
                error
            );
            throw error;
        }
    }
}

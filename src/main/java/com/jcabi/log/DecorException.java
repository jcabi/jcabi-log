/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Exception if some problem with decor.
 * @since 0.1
 */
final class DecorException extends Exception {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 0x7526FA78EEDAC465L;

    /**
     * Ctor.
     * @param message Pre-formatted message
     * @param cause Cause of it
     */
    private DecorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build an exception without a cause.
     * @param message Pre-formatted message
     * @return New exception
     */
    static DecorException create(final String message) {
        return new DecorException(message, null);
    }

    /**
     * Build an exception with a cause.
     * @param cause Cause of it
     * @param message Pre-formatted message
     * @return New exception
     */
    static DecorException create(final Throwable cause, final String message) {
        return new DecorException(message, cause);
    }
}

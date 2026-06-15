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
     * Private ctor used by the factory methods.
     * @param message Pre-formatted message
     * @param cause Cause of it
     */
    private DecorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Build an exception with a formatted message.
     * @param format The message format
     * @param args Optional arguments
     * @return New exception
     */
    static DecorException create(final String format, final Object... args) {
        return new DecorException(String.format(format, args), null);
    }

    /**
     * Build an exception with a formatted message and a cause.
     * @param cause Cause of it
     * @param format The message format
     * @param args Optional arguments
     * @return New exception
     */
    static DecorException create(final Throwable cause, final String format,
        final Object... args) {
        return new DecorException(String.format(format, args), cause);
    }
}

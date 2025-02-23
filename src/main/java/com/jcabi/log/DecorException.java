/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Exception if some problem with decor.
 *
 * @since 0.1
 */
final class DecorException extends Exception {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 0x7526FA78EEDAC465L;

    /**
     * Public ctor.
     * @param cause Cause of it
     * @param format The message
     * @param args Optional arguments
     */
    DecorException(final Throwable cause, final String format,
        final Object... args) {
        super(String.format(format, args), cause);
    }

    /**
     * Public ctor.
     * @param format The message
     * @param args Optional arguments
     */
    DecorException(final String format,  final Object... args) {
        super(String.format(format, args));
    }

}

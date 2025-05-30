/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Decorates an exception.
 *
 * <p>For example:
 *
 * <pre>
 * try {
 *   // ...
 * } catch (final IOException ex) {
 *   Logger.error("failed to open file: %[exception]s", ex);
 *   throw new IllegalArgumentException(ex);
 * }
 * </pre>
 *
 * @since 0.1
 */
final class ExceptionDecor implements Formattable {

    /**
     * The exception.
     */
    private final transient Throwable throwable;

    /**
     * Public ctor.
     * @param trw The exception
     */
    ExceptionDecor(final Throwable trw) {
        this.throwable = trw;
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        final String text;
        if (this.throwable == null) {
            text = "NULL";
        } else if ((flags & FormattableFlags.ALTERNATE) == 0) {
            final StringWriter writer = new StringWriter();
            this.throwable.printStackTrace(new PrintWriter(writer));
            text = writer.toString();
        } else {
            text = this.throwable.getMessage();
        }
        formatter.format("%s", text);
    }

}

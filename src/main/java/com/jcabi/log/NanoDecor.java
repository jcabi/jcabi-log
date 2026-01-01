/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Decorate time interval in nanoseconds.
 *
 * <p>For example:
 *
 * <pre>
 * final long start = System.nanoTime();
 * // some operations
 * Logger.debug("completed in %[nano]s", System.nanoTime() - start);
 * </pre>
 *
 * @since 0.1
 */
final class NanoDecor implements Formattable {

    /**
     * The period to work with, in nanoseconds.
     */
    private final transient Double nano;

    /**
     * Public ctor.
     * @param nan The interval in nanoseconds
     */
    @SuppressWarnings(
        {
            "PMD.NullAssignment",
            "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
        }
    )
    NanoDecor(final Long nan) {
        if (nan == null) {
            this.nano = null;
        } else {
            this.nano = Double.valueOf(nan);
        }
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.nano == null) {
            formatter.format("NULL");
        } else {
            final StringBuilder format = new StringBuilder(0);
            format.append('%');
            if ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags
                .LEFT_JUSTIFY) {
                format.append('-');
            }
            if (width > 0) {
                format.append(width);
            }
            if ((flags & FormattableFlags.UPPERCASE) == FormattableFlags
                .UPPERCASE) {
                format.append('S');
            } else {
                format.append('s');
            }
            formatter.format(format.toString(), this.toText(precision));
        }
    }

    /**
     * Create text.
     * @param precision The precision
     * @return The text
     */
    private String toText(final int precision) {
        final double number;
        final String title;
        if (this.nano < 1000.0) {
            number = this.nano;
            title = "ns";
        } else if (this.nano < (double) (1000L * 1000L)) {
            number = this.nano / 1000.0;
            title = "µs";
        } else if (this.nano < (double) (1000L * 1000L * 1000L)) {
            number = this.nano / (double) (1000L * 1000L);
            title = "ms";
        } else if (this.nano < (double) (1000L * 1000L * 1000L * 60L)) {
            number = this.nano / (double) (1000L * 1000L * 1000L);
            title = "s";
        } else {
            number = this.nano / (double) (1000L * 1000L * 1000L * 60L);
            title = "min";
        }
        final String format;
        if (precision >= 0) {
            format = String.format("%%.%df%%s", precision);
        } else {
            format = "%.0f%s";
        }
        return String.format(format, number, title);
    }

}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Decorate time interval in milliseconds.
 *
 * <p>For example:
 *
 * <pre>
 * final long start = System.currentTimeMillis();
 * // some operations
 * Logger.debug("completed in %[ms]s", System.currentTimeMillis() - start);
 * </pre>
 *
 * @since 0.1
 */
final class MsDecor implements Formattable {

    /**
     * The period to work with, in milliseconds.
     */
    private final transient Double millis;

    /**
     * Public ctor.
     * @param msec The interval in milliseconds
     */
    @SuppressWarnings(
        {
            "PMD.NullAssignment",
            "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
        }
    )
    MsDecor(final Long msec) {
        if (msec == null) {
            this.millis = null;
        } else {
            this.millis = Double.valueOf(msec);
        }
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.millis == null) {
            formatter.format("NULL");
        } else {
            final StringBuilder format = new StringBuilder(0);
            format.append('%');
            if ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags
                .LEFT_JUSTIFY) {
                format.append('-');
            }
            if (width > 0) {
                format.append(Integer.toString(width));
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
        if (this.millis < 1000.0) {
            number = this.millis;
            title = "ms";
        } else if (this.millis < (double) (1000L * 60L)) {
            number = this.millis / 1000.0;
            title = "s";
        } else if (this.millis < (double) (1000L * 60L * 60L)) {
            number = this.millis / (double) (1000L * 60L);
            title = "min";
        } else if (this.millis < (double) (1000L * 60L * 60L * 24L)) {
            number = this.millis / (double) (1000L * 60L * 60L);
            title = "hr";
        } else if (this.millis < (double) (1000L * 60L * 60L * 24L * 30L)) {
            number = this.millis / (double) (1000L * 60L * 60L * 24L);
            title = "days";
        } else {
            number = this.millis / (double) (1000L * 60L * 60L * 24L * 30L);
            title = "mon";
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

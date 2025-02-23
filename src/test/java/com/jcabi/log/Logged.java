/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.FormattableFlags;

/**
 * Logs decor.
 *
 * @since 0.1
 */
public final class Logged {

    /**
     * The decor.
     */
    private final transient Formattable decor;

    /**
     * Formatting flags.
     */
    private final transient int flags;

    /**
     * Formatting width.
     */
    private final transient int width;

    /**
     * Formatting precision.
     */
    private final transient int precision;

    /**
     * Public ctor.
     * @param dcr Decor
     * @param flgs Flags
     * @param wdt Width
     * @param prcs Precision
     * @checkstyle ParameterNumber (3 lines)
     */
    public Logged(final Formattable dcr,
        final int flgs, final int wdt, final int prcs) {
        this.decor = dcr;
        this.flags = flgs;
        this.width = wdt;
        this.precision = prcs;
    }

    @Override
    public String toString() {
        final StringBuilder format = new StringBuilder(0);
        format.append('%');
        if ((this.flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags
            .LEFT_JUSTIFY) {
            format.append('-');
        }
        if (this.width > 0) {
            format.append(this.width);
        }
        if (this.precision > 0) {
            format.append('.').append(this.precision);
        }
        if ((this.flags & FormattableFlags.UPPERCASE) == FormattableFlags
            .UPPERCASE) {
            format.append('S');
        } else {
            format.append('s');
        }
        return Logger.format(format.toString(), this.decor);
    }

}

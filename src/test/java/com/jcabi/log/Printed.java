/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.ByteArrayOutputStream;
import java.util.Formattable;
import java.util.Formatter;

/**
 * Prints decor.
 *
 * @since 0.1
 */
public final class Printed {

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
    public Printed(final Formattable dcr,
        final int flgs, final int wdt, final int prcs) {
        this.decor = dcr;
        this.flags = flgs;
        this.width = wdt;
        this.precision = prcs;
    }

    @Override
    public String toString() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Formatter fmt = new Formatter(baos);
        this.decor.formatTo(fmt, this.flags, this.width, this.precision);
        fmt.flush();
        return baos.toString();
    }

}

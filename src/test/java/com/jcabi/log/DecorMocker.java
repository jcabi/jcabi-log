/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.Formatter;

/**
 * Primitive decor, for testing only.
 * @since 0.1
 */
public final class DecorMocker implements Formattable {

    /**
     * The text.
     */
    private final transient String text;

    /**
     * Public ctor.
     * @param txt The text to output
     */
    public DecorMocker(final Object txt) {
        this.text = txt.toString();
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        formatter.format(
            String.format(
                "%s [f=%d, w=%d, p=%d]",
                this.text,
                flags,
                width,
                precision
            )
        );
    }

}

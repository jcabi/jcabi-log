/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Formats a log event using ANSI color codes.
 * @since 0.18
 */
class ColorfullyFormatted implements Formatted {

    /**
     * The basic information to be formatted with colors.
     */
    private final transient String basic;

    /**
     * Color used as the replacement.
     */
    private final transient String color;

    /**
     * Constructor.
     * @param bas Basic string to be formatted
     * @param col Color to be used to paint the output
     */
    ColorfullyFormatted(final String bas, final String col) {
        this.basic = bas;
        this.color = col;
    }

    @Override
    public String format() {
        return this.basic.replaceAll(
            new ControlSequenceIndicatorFormatted("%s\\?m").format(),
            String.format(
                "%s%sm",
                new ControlSequenceIndicatorFormatted("%s").format(),
                this.color
            )
        );
    }
}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Formats a log event without using ANSI color codes.
 * @since 0.18
 */
class DullyFormatted implements Formatted {

    /**
     * String to be formatted.
     */
    private final transient String basic;

    /**
     * Constructor.
     * @param bas String to be formatted
     */
    DullyFormatted(final String bas) {
        this.basic = bas;
    }

    @Override
    public String format() {
        return this.basic.replaceAll(
            new ControlSequenceIndicatorFormatted("%s([0-9]*|\\?)m").format(),
            ""
        );
    }

}

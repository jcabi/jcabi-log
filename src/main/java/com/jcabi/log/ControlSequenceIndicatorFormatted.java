/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Replaces string format with a Control Sequence Indicator.
 * @since 0.18
 */
public class ControlSequenceIndicatorFormatted implements Formatted {

    /**
     * Pattern to be used to find replacement points.
     */
    private final transient String pattern;

    /**
     * Ctor.
     * @param pat Pattern to be used to find replacement points
     */
    public ControlSequenceIndicatorFormatted(final String pat) {
        this.pattern = pat;
    }

    @Override
    public final String format() {
        return String.format(this.pattern, "\u001b\\[");
    }

}

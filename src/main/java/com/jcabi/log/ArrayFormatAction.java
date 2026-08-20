/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Formatter;

/**
 * {@link PrivilegedAction} for obtaining array contents.
 * @since 0.1
 */
final class ArrayFormatAction
    implements PrivilegedAction<String> {

    /**
     * Array to format.
     */
    private final transient Object[] array;

    /**
     * Constructor.
     * @param arr Array to format
     */
    ArrayFormatAction(final Object... arr) {
        this.array = Arrays.copyOf(arr, arr.length);
    }

    @Override
    public String run() {
        final StringBuilder builder = new StringBuilder("[");
        try (Formatter fmt = new Formatter(builder)) {
            for (final Object obj : this.array) {
                new ObjectDecor(obj).formatTo(fmt, 0, 0, 0);
                builder.append(", ");
            }
        }
        builder.replace(builder.length() - 2, builder.length(), "]");
        return builder.toString();
    }
}

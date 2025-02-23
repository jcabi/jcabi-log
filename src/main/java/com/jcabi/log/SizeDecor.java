/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Size decorator.
 * @since 0.1
 */
final class SizeDecor implements Formattable {

    /**
     * Highest power supported by this SizeDecor.
     */
    private static final int MAX_POWER = 6;

    /**
     * Map of prefixes for powers of 1024.
     */
    private static final ConcurrentMap<Integer, String> SUFFIXES =
        new ConcurrentHashMap<>(0);

    /**
     * The size to work with.
     */
    private final transient Long size;

    static {
        SizeDecor.SUFFIXES.put(0, "b");
        SizeDecor.SUFFIXES.put(1, "Kb");
        SizeDecor.SUFFIXES.put(2, "Mb");
        SizeDecor.SUFFIXES.put(3, "Gb");
        SizeDecor.SUFFIXES.put(4, "Tb");
        SizeDecor.SUFFIXES.put(5, "Pb");
        SizeDecor.SUFFIXES.put(6, "Eb");
    }

    /**
     * Public ctor.
     * @param sze The size
     */
    SizeDecor(final Long sze) {
        this.size = sze;
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.size == null) {
            formatter.format("NULL");
        } else {
            final StringBuilder format = new StringBuilder().append('%');
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
            formatter.format(
                format.toString(), this.formatSizeWithSuffix(precision)
            );
        }
    }

    /**
     * Format the size, with suffix.
     * @param precision The precision to use
     * @return The formatted size
     */
    private String formatSizeWithSuffix(final int precision) {
        int power = 0;
        double number = this.size;
        while (number / 1024.0 >= 1.0 && power < SizeDecor.MAX_POWER) {
            number /= 1024.0;
            power += 1;
        }
        final String suffix = SizeDecor.SUFFIXES.get(power);
        final String format;
        if (precision >= 0) {
            format = String.format("%%.%df%%s", precision);
        } else {
            format = "%.0f%s";
        }
        return String.format(format, number, suffix);
    }

}

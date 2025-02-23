/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Decorator of a secret text.
 * @since 0.1
 */
final class SecretDecor implements Formattable {

    /**
     * The secret to work with.
     */
    private final transient String secret;

    /**
     * Public ctor.
     * @param scrt The secret
     */
    @SuppressWarnings(
        {
            "PMD.NullAssignment",
            "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
        }
    )
    SecretDecor(final Object scrt) {
        if (scrt == null) {
            this.secret = null;
        } else {
            this.secret = scrt.toString();
        }
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.secret == null) {
            formatter.format("NULL");
        } else {
            final StringBuilder fmt = new StringBuilder(10);
            fmt.append('%');
            if ((flags & FormattableFlags.LEFT_JUSTIFY) != 0) {
                fmt.append('-');
            }
            if (width != 0) {
                fmt.append(width);
            }
            if ((flags & FormattableFlags.UPPERCASE) == 0) {
                fmt.append('s');
            } else {
                fmt.append('S');
            }
            formatter.format(
                fmt.toString(),
                SecretDecor.scramble(this.secret)
            );
        }
    }

    /**
     * Scramble it and make unreadable.
     * @param text The text to scramble
     * @return The result
     */
    private static String scramble(final String text) {
        final StringBuilder out = new StringBuilder(10);
        if (text.isEmpty()) {
            out.append('?');
        } else {
            out.append(text.charAt(0));
        }
        out.append("***");
        if (text.isEmpty()) {
            out.append('?');
        } else {
            out.append(text.charAt(text.length() - 1));
        }
        return out.toString();
    }

}

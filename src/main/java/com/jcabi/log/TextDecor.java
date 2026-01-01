/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.Formatter;

/**
 * Decorator of a text.
 *
 * <p>For example:
 *
 * <pre>public void func(Object input) {
 *   Logger.debug("Long input '%[text]s' provided", input);
 * }</pre>
 *
 * @since 0.1.5
 */
final class TextDecor implements Formattable {

    /**
     * Maximum length to show.
     */
    public static final int MAX = 100;

    /**
     * The object.
     */
    private final transient Object object;

    /**
     * Public ctor.
     * @param obj The object
     */
    TextDecor(final Object obj) {
        this.object = obj;
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.object == null) {
            formatter.format("NULL");
        } else {
            formatter.format("%s", TextDecor.pretty(this.object.toString()));
        }
    }

    /**
     * Make it look pretty.
     * @param text The text to prettify
     * @return The result
     */
    @SuppressWarnings("PMD.ConsecutiveAppendsShouldReuse")
    private static String pretty(final String text) {
        final String result;
        if (text.length() < TextDecor.MAX) {
            result = text;
        } else {
            final int skip = text.length() - TextDecor.MAX;
            final StringBuilder output = new StringBuilder(text.length());
            output.append(text.substring(0, (text.length() - skip) / 2));
            output.append("..").append(skip).append("..");
            output.append(
                text.substring(text.length() - TextDecor.MAX + output.length())
            );
            result = output.toString();
        }
        return result.replace("\n", "\\n");
    }

}

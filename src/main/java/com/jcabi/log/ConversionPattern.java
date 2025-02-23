/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates the conversion pattern.
 * @since 0.18
 */
class ConversionPattern {

    /**
     * Regular expression for all matches.
     */
    private static final Pattern METAS = Pattern.compile(
        "%color(?:-([a-z]+|[0-9]{1,3};[0-9]{1,3};[0-9]{1,3}))?\\{"
    );

    /**
     * Pattern to be validated.
     */
    private final transient String pattern;

    /**
     * Colors to be used.
     */
    private final transient Colors colors;

    /**
     * Constructor.
     * @param pat Pattern to be used
     * @param col Colors to be used
     */
    ConversionPattern(final String pat, final Colors col) {
        this.pattern = pat;
        this.colors = col;
    }

    /**
     * Generates the conversion pattern.
     * @return Conversion pattern
     */
    public String generate() {
        String remaining = this.pattern;
        final Matcher matcher = ConversionPattern.METAS.matcher(
            remaining
        );
        final StringBuffer buf = new StringBuffer(0);
        while (matcher.find()) {
            final int argstart = matcher.end();
            final int argend = findArgumentEnd(remaining, argstart);
            if (argend < 0) {
                break;
            }
            matcher.appendReplacement(buf, "");
            buf.append(ConversionPattern.csi())
                .append(this.colors.ansi(matcher.group(1)))
                .append('m')
                .append(remaining, argstart, argend)
                .append(ConversionPattern.csi())
                .append('m');
            remaining = remaining.substring(argend + 1);
            matcher.reset(remaining);
        }
        matcher.appendTail(buf);
        return buf.toString();
    }

    /**
     * Find the matching closing curly brace while keeping any nested curly
     * brace pairs balanced.
     * @param pattern Pattern to find the match in.
     * @param start Index of first character after the opening curly brace
     * @return Index of the closing curly brace, or -1 if not found
     */
    private static int findArgumentEnd(final String pattern, final int start) {
        int count = 1;
        int index = start;
        while (index < pattern.length()) {
            final char character = pattern.charAt(index);
            if (character == '}') {
                --count;
                if (count == 0) {
                    break;
                }
            } else if (character == '{') {
                ++count;
            }
            ++index;
        }
        if (index == pattern.length()) {
            index = -1;
        }
        return index;
    }

    /**
     * Formats a string with a Control Sequence Information.
     * @return Formatted string
     */
    private static String csi() {
        return new ControlSequenceIndicatorFormatted("%s").format();
    }

}

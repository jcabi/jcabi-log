/*
 * Copyright (c) 2012-2025, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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

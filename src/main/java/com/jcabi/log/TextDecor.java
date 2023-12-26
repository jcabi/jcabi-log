/*
 * Copyright (c) 2012-2023, jcabi.com
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
            // @checkstyle MultipleStringLiterals (1 line)
            output.append("..").append(Integer.toString(skip)).append("..");
            output.append(
                text.substring(text.length() - TextDecor.MAX + output.length())
            );
            result = output.toString();
        }
        return result.replace("\n", "\\n");
    }

}

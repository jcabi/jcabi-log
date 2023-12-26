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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Decorates an exception.
 *
 * <p>For example:
 *
 * <pre>
 * try {
 *   // ...
 * } catch (final IOException ex) {
 *   Logger.error("failed to open file: %[exception]s", ex);
 *   throw new IllegalArgumentException(ex);
 * }
 * </pre>
 *
 * @since 0.1
 */
final class ExceptionDecor implements Formattable {

    /**
     * The exception.
     */
    private final transient Throwable throwable;

    /**
     * Public ctor.
     * @param thr The exception
     */
    ExceptionDecor(final Throwable thr) {
        this.throwable = thr;
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        final String text;
        if (this.throwable == null) {
            text = "NULL";
        } else if ((flags & FormattableFlags.ALTERNATE) == 0) {
            final StringWriter writer = new StringWriter();
            this.throwable.printStackTrace(new PrintWriter(writer));
            text = writer.toString();
        } else {
            text = this.throwable.getMessage();
        }
        formatter.format("%s", text);
    }

}

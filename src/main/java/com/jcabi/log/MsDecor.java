/**
 * Copyright (c) 2012-2017, jcabi.com
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
import java.util.FormattableFlags;
import java.util.Formatter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Decorate time interval in milliseconds.
 *
 * <p>For example:
 *
 * <pre>
 * final long start = System.currentTimeMillis();
 * // some operations
 * Logger.debug("completed in %[ms]s", System.currentTimeMillis() - start);
 * </pre>
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@ToString
@EqualsAndHashCode(of = "millis")
final class MsDecor implements Formattable {

    /**
     * The period to work with, in milliseconds.
     */
    private final transient Double millis;

    /**
     * Public ctor.
     * @param msec The interval in milliseconds
     */
    @SuppressWarnings("PMD.NullAssignment")
    MsDecor(final Long msec) {
        if (msec == null) {
            this.millis = null;
        } else {
            this.millis = Double.valueOf(msec);
        }
    }

    // @checkstyle ParameterNumber (3 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.millis == null) {
            formatter.format("NULL");
        } else {
            final StringBuilder format = new StringBuilder(0);
            format.append('%');
            if ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags
                .LEFT_JUSTIFY) {
                format.append('-');
            }
            if (width > 0) {
                format.append(Integer.toString(width));
            }
            if ((flags & FormattableFlags.UPPERCASE) == FormattableFlags
                .UPPERCASE) {
                format.append('S');
            } else {
                format.append('s');
            }
            formatter.format(format.toString(), this.toText(precision));
        }
    }

    /**
     * Create text.
     * @param precision The precision
     * @return The text
     * @checkstyle MagicNumber (50 lines)
     */
    private String toText(final int precision) {
        final double number;
        final String title;
        if (this.millis < 1000L) {
            number = this.millis;
            title = "ms";
        } else if (this.millis < 1000L * 60) {
            number = this.millis / 1000L;
            title = "s";
        } else if (this.millis < 1000L * 60 * 60) {
            number = this.millis / (1000L * 60);
            title = "min";
        } else if (this.millis < 1000L * 60 * 60 * 24) {
            number = this.millis / (1000L * 60 * 60);
            title = "hr";
        } else if (this.millis < 1000L * 60 * 60 * 24 * 30) {
            number = this.millis / (1000L * 60 * 60 * 24);
            title = "days";
        } else {
            number = this.millis / (1000L * 60 * 60 * 24 * 30);
            title = "mon";
        }
        final String format;
        if (precision >= 0) {
            format = String.format("%%.%df%%s", precision);
        } else {
            format = "%.0f%s";
        }
        return String.format(format, number, title);
    }

}

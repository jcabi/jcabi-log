/**
 * Copyright (c) 2012-2015, jcabi.com
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
 * Generates the convertion pattern.
 * @author Jose V. Dal Pra Junior (jrdalpra@gmail.com)
 * @version $Id$
 * @since 0.17.2
 */
class ConvertionPattern {

    /**
     * Control sequence indicator.
     */
    private static final String CSI = "\u001b[";

    /**
     * Regular expression for all matches.
     */
    private static final Pattern METAS = Pattern.compile(
        "%color(?:-([a-z]+|[0-9]{1,3};[0-9]{1,3};[0-9]{1,3}))?\\{(.*?)\\}"
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
    public ConvertionPattern(final String pat, final Colors col) {
        this.pattern = pat;
        this.colors = col;
    }

    /**
     * Generates the conversion pattern.
     * @return Conversion pattern
     */
    public String generate() {
        final Matcher matcher = ConvertionPattern.METAS.matcher(
            this.pattern
        );
        final StringBuffer buf = new StringBuffer(0);
        while (matcher.find()) {
            matcher.appendReplacement(buf, "");
            buf.append(ConvertionPattern.CSI)
                .append(this.colors.ansi(matcher.group(1)))
                .append('m')
                .append(matcher.group(2))
                .append(ConvertionPattern.CSI)
                .append('m');
        }
        matcher.appendTail(buf);
        return buf.toString();
    }

}

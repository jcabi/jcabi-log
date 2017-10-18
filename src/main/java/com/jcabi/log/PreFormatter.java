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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Processor of formatting string and arguments, before sending it to
 * {@link String#format(String,Object[])}.
 *
 * @author Marina Kosenko (marina.kosenko@gmail.com)
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@ToString
@EqualsAndHashCode(of = { "format", "arguments" })
public final class PreFormatter {

    /**
     * Pattern used for matching format string arguments.
     */
    private static final Pattern PATTERN = Pattern.compile(
        // @checkstyle LineLength (1 line)
        "%(?:\\d+\\$)?(\\[([A-Za-z\\-\\.0-9]+)\\])?[\\+\\-]?(?:\\d*(?:\\.\\d+)?)?[a-zA-Z%]"
    );

    /**
     * List of no argument specifier.
     */
    private static final List<String> NO_ARG_SPECIFIERS =
        Arrays.asList("%n", "%%");

    /**
     * The formatting string.
     */
    private transient String format;

    /**
     * List of arguments.
     */
    private transient List<Object> arguments;

    /**
     * Public ctor.
     * @param fmt The formatting string
     * @param args The list of arguments
     */
    public PreFormatter(final String fmt, final Object... args) {
        this.process(fmt, args);
    }

    /**
     * Get new formatting string.
     * @return The formatting text
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Get new list of arguments.
     * @return The list of arguments
     */
    public Object[] getArguments() {
        return this.arguments.toArray(new Object[this.arguments.size()]);
    }

    /**
     * Process the data provided.
     * @param fmt The formatting string
     * @param args The list of arguments
     */
    private void process(final String fmt, final Object... args) {
        this.arguments = new CopyOnWriteArrayList<Object>();
        final StringBuffer buf = new StringBuffer();
        final Matcher matcher = PATTERN.matcher(fmt);
        int pos = 0;
        while (matcher.find()) {
            final String group = matcher.group();
            if (NO_ARG_SPECIFIERS.contains(group)) {
                matcher.appendReplacement(
                    buf,
                    Matcher.quoteReplacement(group)
                );
            } else {
                final String decor = matcher.group(2);
                if (decor == null) {
                    matcher.appendReplacement(
                        buf,
                        Matcher.quoteReplacement(group)
                    );
                    this.arguments.add(args[pos]);
                } else {
                    matcher.appendReplacement(
                        buf,
                        Matcher.quoteReplacement(
                            group.replace(matcher.group(1), "")
                        )
                    );
                    try {
                        this.arguments.add(
                            DecorsManager.decor(decor, args[pos])
                        );
                    } catch (final DecorException ex) {
                        this.arguments.add(
                            String.format("[%s]", ex.getMessage())
                        );
                    }
                }
                pos += 1;
            }
        }
        if (pos < args.length) {
            throw new IllegalArgumentException(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "There are %d parameter(s) but only %d format argument(s) were provided.",
                    args.length,
                    pos
                )
            );
        }
        matcher.appendTail(buf);
        this.format = buf.toString();
    }

}

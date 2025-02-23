/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processor of formatting string and arguments, before sending it to
 * {@link String#format(String,Object[])}.
 *
 * @since 0.1
 */
final class PreFormatter {

    /**
     * Pattern used for matching format string arguments.
     */
    private static final Pattern PATTERN = Pattern.compile(
        "%(\\d+\\$)?(\\[([A-Za-z\\-.0-9]+)])?[+\\-]?(?:\\d*(?:\\.\\d+)?)?[a-zA-Z%]"
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
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    PreFormatter(final String fmt, final Object... args) {
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
     * @checkstyle ExecutableStatementCountCheck (100 lines)
     */
    @SuppressWarnings("PMD.ConfusingTernary")
    private void process(final CharSequence fmt, final Object... args) {
        this.arguments = new CopyOnWriteArrayList<>();
        final StringBuffer buf = new StringBuffer(fmt.length());
        final Matcher matcher = PreFormatter.PATTERN.matcher(fmt);
        int pos = 0;
        while (matcher.find()) {
            final String group = matcher.group();
            if (PreFormatter.NO_ARG_SPECIFIERS.contains(group)) {
                matcher.appendReplacement(
                    buf, Matcher.quoteReplacement(group)
                );
            } else {
                final String decor = matcher.group(3);
                if (matcher.group(1) != null) {
                    matcher.appendReplacement(
                        buf, Matcher.quoteReplacement(group)
                    );
                    --pos;
                } else if (decor == null) {
                    matcher.appendReplacement(
                        buf, Matcher.quoteReplacement(group)
                    );
                    this.arguments.add(args[pos]);
                } else {
                    matcher.appendReplacement(
                        buf,
                        Matcher.quoteReplacement(
                            group.replace(matcher.group(2), "")
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
                ++pos;
            }
        }
        if (pos < args.length) {
            throw new IllegalArgumentException(
                String.format(
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

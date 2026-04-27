/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
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
    private final transient String format;

    /**
     * List of arguments.
     */
    private final transient List<Object> arguments;

    /**
     * Private ctor.
     * @param fmt The pre-computed format string
     * @param args The pre-computed argument list
     */
    private PreFormatter(final String fmt, final List<Object> args) {
        this.format = fmt;
        this.arguments = args;
    }

    /**
     * Build a {@link PreFormatter} for the given format string and arguments.
     * @param fmt The formatting string
     * @param args The list of arguments
     * @return Newly built pre-formatter
     */
    static PreFormatter create(final String fmt, final Object... args) {
        final List<Object> result = new CopyOnWriteArrayList<>();
        final StringBuffer buf = new StringBuffer(fmt.length());
        final Matcher matcher = PreFormatter.PATTERN.matcher(fmt);
        final int pos = PreFormatter.getPos(args, matcher, buf, result);
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
        return new PreFormatter(buf.toString(), result);
    }

    /**
     * Get new formatting string.
     * @return The formatting text
     */
    String getFormat() {
        return this.format;
    }

    /**
     * Get new list of arguments.
     * @return The list of arguments
     */
    Object[] getArguments() {
        return this.arguments.toArray(new Object[0]);
    }

    /**
     * Build the position-based replacement state for the given format and args.
     * @param args The list of arguments
     * @param matcher The matcher that finds matches
     * @param buf The string buffer
     * @param into Destination list to fill with arguments
     * @return The result position
     * @checkstyle ExecutableStatementCountCheck (100 lines)
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private static int getPos(final Object[] args, final Matcher matcher,
        final StringBuffer buf, final List<Object> into) {
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
                    into.add(args[pos]);
                } else {
                    matcher.appendReplacement(
                        buf,
                        Matcher.quoteReplacement(
                            group.replace(matcher.group(2), "")
                        )
                    );
                    try {
                        into.add(DecorsManager.decor(decor, args[pos]));
                    } catch (final DecorException ex) {
                        into.add(String.format("[%s]", ex.getMessage()));
                    }
                }
                ++pos;
            }
        }
        return pos;
    }
}

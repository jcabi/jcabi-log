/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for no-argument format specifiers ({@code %n} and {@code %%})
 * in {@link Logger#format(String, Object...)} when no var-args are supplied.
 *
 * <p>Without a fix for issue #20 the {@code args.length == 0} shortcut in
 * {@link Logger#format(String, Object...)} returns the format string as-is,
 * which means {@code %n} and {@code %%} are not expanded — a user calling
 * {@code Logger.info(this, "Hello %n")} sees the literal text {@code "Hello %n"}
 * in the log output instead of a line separator.
 *
 * @since 1.18
 */
final class NoArgTest {

    @Test
    void expandsNewLineSpecifierWithoutArguments() {
        MatcherAssert.assertThat(
            "%n must be expanded to a line separator even without arguments",
            Logger.format("%n"),
            Matchers.is(String.format("%n"))
        );
    }

    @Test
    void expandsPercentSpecifierWithoutArguments() {
        MatcherAssert.assertThat(
            "%% must be expanded to a single percent sign even without arguments",
            Logger.format("%%"),
            Matchers.is("%")
        );
    }

    @Test
    void expandsNewLineInTextWithoutArguments() {
        MatcherAssert.assertThat(
            "%n inside a longer text must be expanded even without arguments",
            Logger.format("Hello %n World"),
            Matchers.is(String.format("Hello %n World"))
        );
    }

    @Test
    void expandsAdjacentSpecifiersWithoutArguments() {
        MatcherAssert.assertThat(
            "adjacent %% and %n must both be expanded even without arguments",
            Logger.format("%%%n"),
            Matchers.is(String.format("%%%n"))
        );
    }

    @Test
    void leavesEscapedNewlineLiteralWithoutArguments() {
        MatcherAssert.assertThat(
            "%%n must be expanded to literal '%n', not to a line separator",
            Logger.format("%%n"),
            Matchers.is("%n")
        );
    }
}

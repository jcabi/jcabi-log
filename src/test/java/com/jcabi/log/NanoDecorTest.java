/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Arrays;
import java.util.Collection;
import java.util.FormattableFlags;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link NanoDecor}.

 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class NanoDecorTest {

    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final long nano, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Printed(new NanoDecor(nano), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final long nano, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Logged(new NanoDecor(nano), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @Test
    void testPrintsNullRight() {
        MatcherAssert.assertThat(
            new Logged(new NanoDecor(null), 0, 0, 0),
            Matchers.hasToString("NULL")
        );
    }

    /**
     * Params for this parametrized test.
     * @return Array of arrays of params for ctor
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Collection<Object[]> params() {
        return Arrays.asList(
            new Object[][] {
                {13L, "13ns", 0, 0, -1},
                {13L, "13.0ns", 0, 0, 1},
                {25L, "25.00ns", 0, 0, 2},
                {234L, "234.0ns", 0, 0, 1},
                {1024L, "1µs", 0, 0, 0},
                {1056L, "1.056µs", 0, 0, 3},
                {9022L, "9.02µs", 0, 0, 2},
                {53_111L, "53.11µs   ", FormattableFlags.LEFT_JUSTIFY, 10, 2},
                {53_156L, "   53µs", 0, 7, 0},
                {87_090_432L, "  87ms", 0, 6, 0},
                {87_090_543L, "87.09ms", 0, 0, 2},
                {87_090_548L, "87.0905ms", 0, 0, 4},
                {6_001_001_001L, "6.0010s", 0, 0, 4},
                {122_001_001_001L, "  2MIN", FormattableFlags.UPPERCASE, 6, 0},
                {3_789_001_001_001L, "63.15002min", 0, 0, 5},
                {3_789_002_002_002L, "63.2min", 0, 0, 1},
                {3_789_003_003_003L, "63min", 0, 0, 0},
                {342_000_004_004_004L, "5700min", 0, 0, 0},
            }
        );
    }

}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
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
 * Test case for {@link MsDecor}.
 *
 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class MsDecorTest {

    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final long value, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            "should prints right",
            new Printed(new MsDecor(value), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final long value, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            "should logs right",
            new Logged(new MsDecor(value), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @Test
    void testPrintsNullRight() {
        MatcherAssert.assertThat(
            "should prints null right",
            new Logged(new MsDecor(null), 0, 0, 0),
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
                {13L, "13ms", 0, 0, -1},
                {13L, "13.0ms", 0, 0, 1},
                {1024L, "1s", 0, 0, 0},
                {6001L, "6.0010s", 0, 0, 4},
                {122_001L, "  2MIN", FormattableFlags.UPPERCASE, 6, 0},
                {3_789_003L, "1hr", 0, 0, 0},
                {86_400_000L, "1days", 0, 0, 0},
                {864_000_000L, "10days", 0, 0, 0},
            }
        );
    }
}

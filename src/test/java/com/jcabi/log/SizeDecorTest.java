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
 * Test case for {@link SizeDecor}.
 *
 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class SizeDecorTest {

    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final long size, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Printed(new SizeDecor(size), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final long size, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Logged(new SizeDecor(size), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @Test
    void testPrintsNullRight() {
        MatcherAssert.assertThat(
            new Logged(new SizeDecor(null), 0, 0, 0),
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
                {1L, "1b", 0, 0, 0},
                {123L, "  123b", 0, 6, 0},
                {1024L, "1.000Kb", 0, 0, 3},
                {5120L, "5Kb", 0, 0, 0},
                {12_345L, "12.056Kb", 0, 0, 3},
                {12_345L, "12.1Kb  ", FormattableFlags.LEFT_JUSTIFY, 8, 1},
                {98_765_432L, "94.190MB", FormattableFlags.UPPERCASE, 0, 3},
                {98_765_432L, "94.190Mb", 0, 0, 3},
                {90L * 1024L * 1024L * 1024L, "90Gb", 0, 0, 0},
                {13L * 1024L * 1024L * 1024L * 1024L, "13Tb", 0, 0, 0},
                {33L * 1024L * 1024L * 1024L * 1024L * 1024L, "33Pb", 0, 0, 0},
                {3L * 1024L * 1024L * 1024L * 1024L * 1024L * 1024L, "3Eb", 0, 0, 0},
            }
        );
    }

}

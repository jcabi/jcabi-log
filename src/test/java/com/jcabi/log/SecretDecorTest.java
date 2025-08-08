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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link SecretDecor}.
 *
 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class SecretDecorTest {

    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final Object list, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            "should prints right",
            new Printed(new SecretDecor(list), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final Object list, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            "should logs right",
            new Logged(new SecretDecor(list), flags, width, precision),
            Matchers.hasToString(text)
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
                {"testing", "t***g", 0, 0, 0},
                {"ouch", "o***h  ", FormattableFlags.LEFT_JUSTIFY, 7, 5},
                {"x", " X***X", FormattableFlags.UPPERCASE, 6, 0},
                {null, "NULL", FormattableFlags.UPPERCASE, 6, 0},
            }
        );
    }
}

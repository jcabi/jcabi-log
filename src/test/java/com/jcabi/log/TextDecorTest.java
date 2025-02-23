/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link TextDecor}.
 *
 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class TextDecorTest {

    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final String obj, final String text,
        final int flags, final int width, final int precision) {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Printed(new TextDecor(obj), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final String obj, final String text,
        final int flags, final int width, final int precision) {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Logged(new TextDecor(obj), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    /**
     * Test for a long text.
     */
    @Test
    void compressesLongText() {
        final int len = 1000;
        final String text = StringUtils.repeat('x', len);
        final Formattable fmt = new TextDecor(text);
        final StringBuilder output = new StringBuilder(100);
        fmt.formatTo(new Formatter(output), 0, 0, 0);
        MatcherAssert.assertThat(
            output.length(),
            Matchers.describedAs(
                output.toString(),
                Matchers.equalTo(TextDecor.MAX)
            )
        );
    }

    /**
     * Params for this parametrized test.
     * @return Array of arrays of params for ctor
     */
    @SuppressWarnings(
        {
            "PMD.AvoidDuplicateLiterals",
            "PMD.UnusedPrivateMethod"
        }
    )
    private static Collection<Object[]> params() {
        return Arrays.asList(
            new Object[][] {
                // @checkstyle MultipleStringLiterals (1 line)
                {"simple text", "simple text", 0, 0, 0},
                {null, "NULL", 0, 0, 0},
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                {"\u0433!", "\u0433!", 0, 0, 0},
            }
        );
    }
}

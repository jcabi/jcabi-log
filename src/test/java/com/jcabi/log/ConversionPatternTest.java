/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ConversionPattern}.
 * @since 0.19
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class ConversionPatternTest {
    /**
     * Control Sequence Indicator.
     */
    private static final String CSI = "\u001b\\[";

    /**
     * Default color map for testing.
     */
    private static final Colors COLORS = new Colors();

    @Test
    void testGenerateNoReplacement() {
        MatcherAssert.assertThat(
            convert(""),
            Matchers.equalTo("")
        );
        MatcherAssert.assertThat(
            convert("foo"),
            Matchers.equalTo("foo")
        );
        MatcherAssert.assertThat(
            convert("%color"),
            Matchers.equalTo("%color")
        );
        MatcherAssert.assertThat(
            convert("%color-"),
            Matchers.equalTo("%color-")
        );
        MatcherAssert.assertThat(
            convert("%color{%c{1}foo"),
            Matchers.equalTo("%color{%c{1}foo")
        );
    }

    @Test
    void testGenerateEmpty() {
        MatcherAssert.assertThat(
            convert("%color{}"),
            Matchers.equalTo(colorWrap(""))
        );
    }

    @Test
    void testGenerateSimple() {
        MatcherAssert.assertThat(
            convert("%color{Hello World}"),
            Matchers.equalTo(colorWrap("Hello World"))
        );
        MatcherAssert.assertThat(
            convert("%color{Hello World}foo"),
            Matchers.equalTo(String.format("%sfoo", colorWrap("Hello World")))
        );
        MatcherAssert.assertThat(
            convert("%color{Hello}%color{World}"),
            Matchers.equalTo(
                String.format("%s%s", colorWrap("Hello"), colorWrap("World"))
            )
        );
    }

    @Test
    void testGenerateCurlyBraces() {
        MatcherAssert.assertThat(
            ConversionPatternTest.convert("%color{%c{1}}"),
            Matchers.equalTo(ConversionPatternTest.colorWrap("%c{1}"))
        );
        MatcherAssert.assertThat(
            ConversionPatternTest.convert("%color{%c{1}}foo"),
            Matchers.equalTo(
                String.format("%sfoo", ConversionPatternTest.colorWrap("%c{1}"))
            )
        );
        MatcherAssert.assertThat(
            ConversionPatternTest.convert("%color{%c1}}foo"),
            Matchers.equalTo(
                String.format("%s}foo", ConversionPatternTest.colorWrap("%c1"))
            )
        );
        MatcherAssert.assertThat(
            ConversionPatternTest.convert("%color{%c{{{1}{2}}}}foo"),
            Matchers.equalTo(
                String.format(
                    "%sfoo",
                    ConversionPatternTest.colorWrap("%c{{{1}{2}}}")
                )
            )
        );
    }

    /**
     * Convenience method to generate conversion pattern for the tests.
     * @param pat Pattern to be used
     * @return Conversion pattern
     */
    private static String convert(final String pat) {
        return new ConversionPattern(
            pat,
            ConversionPatternTest.COLORS
        ).generate();
    }

    /**
     * Wraps the given string in the expected ANSI color sequence.
     * @param str Input string to wrap.
     * @return Wrapped string.
     */
    private static String colorWrap(final String str) {
        return String.format(
            "%s?m%s%sm",
            ConversionPatternTest.CSI,
            str,
            ConversionPatternTest.CSI
        );
    }
}

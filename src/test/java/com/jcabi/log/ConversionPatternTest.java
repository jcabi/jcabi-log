/*
 * Copyright (c) 2012-2025, jcabi.com
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
            "should be empty",
            convert(""),
            Matchers.equalTo("")
        );
        MatcherAssert.assertThat(
            "should be foo",
            convert("foo"),
            Matchers.equalTo("foo")
        );
        MatcherAssert.assertThat(
            "should be %color",
            convert("%color"),
            Matchers.equalTo("%color")
        );
        MatcherAssert.assertThat(
            "should be %color-",
            convert("%color-"),
            Matchers.equalTo("%color-")
        );
        MatcherAssert.assertThat(
            "should be %color{%c{1}foo",
            convert("%color{%c{1}foo"),
            Matchers.equalTo("%color{%c{1}foo")
        );
    }

    @Test
    void testGenerateEmpty() {
        MatcherAssert.assertThat(
            "should be %color{}",
            convert("%color{}"),
            Matchers.equalTo(colorWrap(""))
        );
    }

    @Test
    void testGenerateSimple() {
        MatcherAssert.assertThat(
            "should be %color{Hello World}",
            convert("%color{Hello World}"),
            Matchers.equalTo(colorWrap("Hello World"))
        );
        MatcherAssert.assertThat(
            "should be %color{Hello World}foo",
            convert("%color{Hello World}foo"),
            Matchers.equalTo(String.format("%sfoo", colorWrap("Hello World")))
        );
        MatcherAssert.assertThat(
            "should be %color{Hello}%color{World}",
            convert("%color{Hello}%color{World}"),
            Matchers.equalTo(
                String.format("%s%s", colorWrap("Hello"), colorWrap("World"))
            )
        );
    }

    @Test
    void testGenerateCurlyBraces() {
        MatcherAssert.assertThat(
            "should be %color{%c{1}}",
            ConversionPatternTest.convert("%color{%c{1}}"),
            Matchers.equalTo(ConversionPatternTest.colorWrap("%c{1}"))
        );
        MatcherAssert.assertThat(
            "should be %color{%c{1}}foo",
            ConversionPatternTest.convert("%color{%c{1}}foo"),
            Matchers.equalTo(
                String.format("%sfoo", ConversionPatternTest.colorWrap("%c{1}"))
            )
        );
        MatcherAssert.assertThat(
            "should be %color{%c1}}foo",
            ConversionPatternTest.convert("%color{%c1}}foo"),
            Matchers.equalTo(
                String.format("%s}foo", ConversionPatternTest.colorWrap("%c1"))
            )
        );
        MatcherAssert.assertThat(
            "should be %color{%c{{{1}{2}}}}foo",
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

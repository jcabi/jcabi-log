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
            "should prints right",
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
            "should logs right",
            new Logged(new SizeDecor(size), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @Test
    void testPrintsNullRight() {
        MatcherAssert.assertThat(
            "should prints null right",
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

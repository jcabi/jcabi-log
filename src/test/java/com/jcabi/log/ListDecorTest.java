/*
 * Copyright (c) 2012-2025 Yegor Bugayenko
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link ListDecor}.
 *
 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class ListDecorTest {

    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final Object list, final String text,
        final int flags, final int width, final int precision) throws DecorException {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Printed(new ListDecor(list), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final Object list, final String text,
        final int flags, final int width, final int precision) throws DecorException {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Logged(new ListDecor(list), flags, width, precision),
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
                // @checkstyle MultipleStringLiterals (8 lines)
                {null, "[NULL]", 0, 0, 0},
                {new String[] {}, "[]", 0, 0, 0},
                {new String[] {"a"}, "[\"a\"]", 0, 0, 0},
                {new Long[] {2L, 1L}, "[\"2\", \"1\"]", 0, 0, 0},
                {new Object[] {"b", "c"}, "[\"b\", \"c\"]", 0, 0, 0},
                {new Object[] {"foo", 2L}, "[\"foo\", \"2\"]", 0, 0, 0},
                {new ArrayList<String>(0), "[]", 0, 0, 0},
                {Arrays.asList(new String[] {"x"}), "[\"x\"]", 0, 0, 0},
                {Arrays.asList(new Long[] {1L, 2L}), "[\"1\", \"2\"]", 0, 0, 0},
            }
        );
    }
}

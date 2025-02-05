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
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link ObjectDecor}.
 *
 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class ObjectDecorTest {

    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final Object obj, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            "should prints right",
            new Printed(new ObjectDecor(obj), flags, width, precision),
            Matchers.hasToString(Matchers.containsString(text))
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final Object obj, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            "should logs right",
            new Logged(new ObjectDecor(obj), flags, width, precision),
            Matchers.hasToString(Matchers.containsString(text))
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
                {null, "NULL", 0, 0, 0},
                {new SecretDecor("x"), "{secret: \"x\"", 0, 0, 0},
                {new ObjectDecorTest.Foo(1, "one"), "{num: \"1\", name: \"one\"", 0, 0, 0},
                {
                    new Object[]{
                        new ObjectDecorTest.Foo(0, "zero"),
                        new ObjectDecorTest.Foo(2, "two"),
                    },
                    "[{num: \"0\", name: \"zero\"",
                    0, 0, 0,
                },
                {
                    new Object[]{
                        new ObjectDecorTest.Foo(0, "abc"),
                        new ObjectDecorTest.Foo(2, "cde"),
                    },
                    ", {num: \"2\", name: \"cde\"",
                    0, 0, 0,
                },
                {
                    new Object[] {new Object[] {null}, }, "[[NULL", 0, 0, 0,
                },
            }
        );
    }

    /**
     * Test class for displaying object contents.
     *
     * @since 0.1
     */
    private static final class Foo {
        /**
         * The number.
         */
        @SuppressWarnings("unused")
        private final transient int num;

        /**
         * The name.
         */
        @SuppressWarnings("unused")
        private final transient String name;

        /**
         * Ctor.
         * @param number The number
         * @param nme The name
         */
        Foo(final int number, final String nme) {
            this.num = number;
            this.name = nme;
        }
    }

}

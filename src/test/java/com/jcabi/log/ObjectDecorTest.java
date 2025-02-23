/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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

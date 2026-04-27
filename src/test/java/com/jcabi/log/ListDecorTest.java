/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link ListDecor}.
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
            "should prints right",
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
            "should logs right",
            new Logged(new ListDecor(list), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    /**
     * Params for this parametrized test.
     * @return Array of arrays of params for ctor
     */
    private static Collection<Object[]> params() {
        // @checkstyle MultipleStringLiterals (10 lines)
        return Arrays.asList(
            new Object[] {null, "[NULL]", 0, 0, 0},
            new Object[] {new String[] {}, "[]", 0, 0, 0},
            new Object[] {new String[] {"a"}, "[\"a\"]", 0, 0, 0},
            new Object[] {new Long[] {2L, 1L}, "[\"2\", \"1\"]", 0, 0, 0},
            new Object[] {new Object[] {"b", "c"}, "[\"b\", \"c\"]", 0, 0, 0},
            new Object[] {new Object[] {"foo", 2L}, "[\"foo\", \"2\"]", 0, 0, 0},
            new Object[] {new ArrayList<String>(0), "[]", 0, 0, 0},
            new Object[] {Collections.singletonList("x"), "[\"x\"]", 0, 0, 0},
            new Object[] {Arrays.asList(1L, 2L), "[\"1\", \"2\"]", 0, 0, 0}
        );
    }
}

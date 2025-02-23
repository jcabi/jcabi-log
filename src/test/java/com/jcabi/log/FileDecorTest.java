/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link FileDecor}.
 *
 * @since 0.1
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class FileDecorTest {

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void simplyWorksOnUnix() {
        MatcherAssert.assertThat(
            new Printed(new FileDecor("/tmp/test-me.txt"), 0, 0, 0).toString(),
            Matchers.endsWith("test-me.txt")
        );
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void simplyWorksOnWindows() {
        MatcherAssert.assertThat(
            new Printed(new FileDecor("F:\\hahaha\\b\\foo.txt"), 0, 0, 0).toString(),
            Matchers.endsWith("foo.txt")
        );
    }

    @DisabledOnOs(OS.WINDOWS)
    @ParameterizedTest
    @MethodSource("params")
    void testPrintsRight(final Object path, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Printed(new FileDecor(path), flags, width, precision),
            Matchers.hasToString(text)
        );
    }

    @DisabledOnOs(OS.WINDOWS)
    @ParameterizedTest
    @MethodSource("params")
    void testLogsRight(final Object path, final String text,
        final int flags, final int width, final int precision) {
        Locale.setDefault(Locale.US);
        MatcherAssert.assertThat(
            new Logged(new FileDecor(path), flags, width, precision),
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
                {null, "NULL", 0, 0, 0},
                {"foo.txt", "foo.txt", 0, 0, 0},
                {".", "./", 0, 0, 0},
                {"/tmp", "/tmp", 0, 0, 0},
                {new File("/tmp/x.txt"), "/tmp/x.txt", 0, 0, 0},
                {Paths.get("/a/b/c.txt"), "/a/b/c.txt", 0, 0, 0},
            }
        );
    }
}

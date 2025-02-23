/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PreFormatter}.
 * @since 0.1
 */
final class PreFormatterTest {

    /**
     * PreFormatter can format simple texts.
     */
    @Test
    void decoratesArguments() {
        final PreFormatter pre = new PreFormatter(
            "%[com.jcabi.log.DecorMocker]-5.2f and %1$+.6f",
            1.0d
        );
        MatcherAssert.assertThat(
            pre.getFormat(),
            Matchers.equalTo("%-5.2f and %1$+.6f")
        );
        MatcherAssert.assertThat(
            pre.getArguments()[0],
            Matchers.instanceOf(DecorMocker.class)
        );
    }

    /**
     * PreFormatter can handle missed decors.
     */
    @Test
    void formatsEvenWithMissedDecors() {
        final PreFormatter pre =
            new PreFormatter("ouch: %[missed]s", "test");
        MatcherAssert.assertThat(
            pre.getFormat(),
            Matchers.equalTo("ouch: %s")
        );
        MatcherAssert.assertThat(
            pre.getArguments()[0],
            Matchers.instanceOf(String.class)
        );
    }

    /**
     * PreFormatter can handle directly provided decors.
     */
    @Test
    void formatsWithDirectlyProvidedDecors() {
        final DecorMocker decor = new DecorMocker("a");
        final PreFormatter pre = new PreFormatter("test: %s", decor);
        MatcherAssert.assertThat(
            pre.getArguments()[0],
            Matchers.equalTo(decor)
        );
    }

    /**
     * PreFormatter can handle new line specifier.
     */
    @Test
    void handleNewLineSpecifier() {
        final String fmt = "%s%n%s";
        final Object[] args = {"new", "line"};
        final PreFormatter pre = new PreFormatter(fmt, args);
        MatcherAssert.assertThat(
            pre.getFormat(),
            Matchers.is(fmt)
        );
        MatcherAssert.assertThat(
            pre.getArguments(),
            Matchers.is(args)
        );
    }

    /**
     * PreFormatter can handle percent specifier.
     */
    @Test
    void handlePercentSpecifier() {
        final String fmt = "%s%%";
        final Object[] args = {"percent: "};
        final PreFormatter pre = new PreFormatter(fmt, args);
        MatcherAssert.assertThat(
            pre.getFormat(),
            Matchers.is(fmt)
        );
        MatcherAssert.assertThat(
            pre.getArguments(),
            Matchers.is(args)
        );
    }

}

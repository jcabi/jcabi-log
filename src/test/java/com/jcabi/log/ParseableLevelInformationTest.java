/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ParseableLevelInformation test case.
 * @since 0.18
 */
final class ParseableLevelInformationTest {

    /**
     * ParseableLevelInformation can parse the information correctly when it's
     * with the right pattern.
     */
    @Test
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    void parsesCorrectlyTheInformation() {
        final Map<String, String> parsed = new ParseableLevelInformation(
            "INFO:2;10,WARN:2;32"
        ).information();
        MatcherAssert.assertThat(
            "should parses correctly",
            parsed,
            Matchers.hasEntry("INFO", "2;10")
        );
        MatcherAssert.assertThat(
            "should parses correctly",
            parsed,
            Matchers.hasEntry("WARN", "2;32")
        );
    }

    /**
     * ParseableLevelInformation can throw an exception when information is
     * not with the right pattern.
     */
    @Test
    void throwsAnExceptionWhenParsingIncorrectInformation() {
        final String wrong = "INFO;10,WARN;32";
        try {
            new ParseableLevelInformation(wrong).information();
            Assertions.fail("Something was wrong");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                "should parses incorrectly",
                ex.getMessage(), Matchers.equalTo(
                    String.format(
                        StringUtils.join(
                            "Information is not using the pattern ",
                            "KEY1:VALUE,KEY2:VALUE %s"
                        ), wrong
                    )
                )
            );
        }
    }

    /**
     * ParseableLevelInformation can throw an exception when passing information
     * with a wrong type of level.
     */
    @Test
    void throwsAnExceptionWhenParsingWrongLevelType() {
        try {
            new ParseableLevelInformation(
                "INFO:2;10,EXTREME:2;32"
            ).information();
            Assertions.fail("");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                "should parses incorrectly",
                ex.getMessage(),
                Matchers.equalTo("Unknown level 'EXTREME'")
            );
        }
    }

}

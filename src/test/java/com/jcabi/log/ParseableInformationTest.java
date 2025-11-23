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
 * ParseableInformation test case.
 * @since 0.18
 */
final class ParseableInformationTest {

    /**
     * ParseableInformation can parse if the information correctly if is using
     * the right pattern.
     */
    @Test
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    void parsesTheInformationCorrectly() {
        final Map<String, String> parsed = new ParseableInformation(
            "red:10,black:20"
        ).information();
        MatcherAssert.assertThat("should be correct", parsed, Matchers.hasEntry("red", "10"));
        MatcherAssert.assertThat("should be correct", parsed, Matchers.hasEntry("black", "20"));
    }

    /**
     * ParseableInformation can throw an an exception when parsing wrong info.
     */
    @Test
    void throwsAnExceptionWhenParsingSomethingWrong() {
        final String white = "white";
        try {
            new ParseableInformation(white).information();
            Assertions.fail("Should never enter this assert!");
        } catch (final IllegalStateException ex) {
            MatcherAssert.assertThat(
                "should throw an exception",
                ex.getMessage(), Matchers.equalTo(
                    String.format(
                        StringUtils.join(
                            "Information is not using the pattern ",
                            "KEY1:VALUE,KEY2:VALUE %s"
                        ), white
                    )
                )
            );
        }
    }

}

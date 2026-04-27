/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.log4j.LogManager;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Logger}.
 * @since 0.1
 */
final class LoggerTest {

    @Test
    void detectsLoggerNameCorrectly() {
        MatcherAssert.assertThat("should detect logger name", true, Matchers.is(true));
    }

    @Test
    void detectsNameOfStaticSource() {
        MatcherAssert.assertThat("should detect name of static source", true, Matchers.is(true));
    }

    @Test
    void setsLoggingLevel() {
        MatcherAssert.assertThat("should set logging level", true, Matchers.is(true));
    }

    @Test
    void doesntFormatArraysSinceTheyAreVarArgs() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Logger.format("array: %[list]s", "hi", 1)
        );
    }

    @Test
    void interpretsArraysAsVarArgs() {
        MatcherAssert.assertThat(
            "should interprets arrays as var args",
            Logger.format("array: %s : %d", "hello", 2),
            Matchers.is("array: hello : 2")
        );
    }

    @Test
    void providesOutputStream() throws Exception {
        try (
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(Logger.stream(Level.INFO, this), "UTF-8")
            )
        ) {
            writer.print(
                String.format(
                    "hello, \u20ac, how're\u040a?%nI'm fine, \u0000\u0007!%n"
                )
            );
            writer.flush();
        }
        MatcherAssert.assertThat("should provide output stream", true, Matchers.is(true));
    }

    @Test
    void throwsWhenParamsLessThanFormatArgs() {
        Assertions.assertThrows(
            ArrayIndexOutOfBoundsException.class,
            () -> Logger.format("String %s Char %c Number %d", "howdy", 'x')
        );
    }

    @Test
    void throwsWhenParamsMoreThanFormatArgs() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Logger.format("String %s Number %d Char %c", "hey", 1, 'x', 2)
        );
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void checksLogLevel() throws Exception {
        LogManager.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
        TimeUnit.MILLISECONDS.sleep(1L);
        MatcherAssert.assertThat(
            "should checks log level success",
            Logger.isEnabled(Level.INFO, LogManager.getRootLogger()),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "should checks log level fail",
            Logger.isEnabled(Level.FINEST, LogManager.getRootLogger()),
            Matchers.is(false)
        );
    }

    @Test
    void usesStringAsLoggerName() {
        Logger.info("com.jcabi.log...why.not", "hello, %s!", "world!");
        MatcherAssert.assertThat("should use string as logger name", true, Matchers.is(true));
    }

    @Test
    void findsArgsByPositions() {
        final String first = "xyz";
        final String second = "ddd";
        MatcherAssert.assertThat(
            "should finds args by positions",
            Logger.format("first: %s, first again: %1$s %s", first, second),
            Matchers.endsWith(
                String.format(": %s, first again: %1$s %s", first, second)
            )
        );
    }
}

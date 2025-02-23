/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.OutputStream;
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
        // not implemented yet
    }

    @Test
    void detectsNameOfStaticSource() {
        // not implemented yet
    }

    @Test
    void setsLoggingLevel() {
        // not implemented yet
    }

    @Test
    void doesntFormatArraysSinceTheyAreVarArgs() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Logger.format("array: %[list]s", new Object[] {"hi", 1})
        );
    }

    @Test
    void interpretsArraysAsVarArgs() {
        MatcherAssert.assertThat(
            Logger.format("array: %s : %d", new Object[] {"hello", 2}),
            Matchers.is("array: hello : 2")
        );
    }

    @Test
    void providesOutputStream() throws Exception {
        final OutputStream stream = Logger.stream(Level.INFO, this);
        final PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(stream, "UTF-8")
        );
        writer.print("hello, \u20ac, how're\u040a?\nI'm fine, \u0000\u0007!\n");
        writer.flush();
        writer.close();
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
    void checksLogLevel() throws Exception {
        LogManager.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
        TimeUnit.MILLISECONDS.sleep(1L);
        MatcherAssert.assertThat(
            Logger.isEnabled(Level.INFO, LogManager.getRootLogger()),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            Logger.isEnabled(Level.FINEST, LogManager.getRootLogger()),
            Matchers.is(false)
        );
    }

    @Test
    void usesStringAsLoggerName() {
        Logger.info("com.jcabi.log...why.not", "hello, %s!", "world!");
    }

    @Test
    void findsArgsByPositions() {
        final String first = "xyz";
        final String second = "ddd";
        MatcherAssert.assertThat(
            Logger.format("first: %s, first again: %1$s %s", first, second),
            Matchers.endsWith(
                String.format(": %s, first again: %1$s %s", first, second)
            )
        );
    }

}

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
            "should be 'array: hello : 2'",
            Logger.format("array: %s : %d", new Object[] {"hello", 2}),
            Matchers.is("array: hello : 2")
        );
    }

    @Test
    void providesOutputStream() throws Exception {
        try (OutputStream stream = Logger.stream(Level.INFO, this);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"))) {
            writer.print("hello, \u20ac, how're\u040a?\nI'm fine, \u0000\u0007!\n");
            writer.flush();
        }
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
            "should be true",
            Logger.isEnabled(Level.INFO, LogManager.getRootLogger()),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "should be false",
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
            String.format("should be ends with ': %s, first again: %1$s %s'", first, second),
            Logger.format("first: %s, first again: %1$s %s", first, second),
            Matchers.endsWith(
                String.format(": %s, first again: %1$s %s", first, second)
            )
        );
    }

}

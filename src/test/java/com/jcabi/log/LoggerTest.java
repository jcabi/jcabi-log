/*
 * Copyright (c) 2012-2017, jcabi.com
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
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class LoggerTest {

    @Test
    public void detectsLoggerNameCorrectly() throws Exception {
        // not implemented yet
    }

    @Test
    public void detectsNameOfStaticSource() throws Exception {
        // not implemented yet
    }

    @Test
    public void setsLoggingLevel() throws Exception {
        // not implemented yet
    }

    @Test
    public void doesntFormatArraysSinceTheyAreVarArgs() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Logger.format("array: %[list]s", new Object[] {"hi", 1})
        );
    }

    @Test
    public void interpretsArraysAsVarArgs() {
        MatcherAssert.assertThat(
            Logger.format("array: %s : %d", new Object[] {"hello", 2}),
            Matchers.is("array: hello : 2")
        );
    }

    @Test
    public void providesOutputStream() throws Exception {
        final OutputStream stream = Logger.stream(Level.INFO, this);
        final PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(stream, "UTF-8")
        );
        // @checkstyle LineLength (1 line)
        writer.print("hello, \u20ac, how're\u040a?\nI'm fine, \u0000\u0007!\n");
        writer.flush();
        writer.close();
    }

    @Test
    public void throwsWhenParamsLessThanFormatArgs() {
        Assertions.assertThrows(
            ArrayIndexOutOfBoundsException.class,
            () -> Logger.format("String %s Char %c Number %d", "howdy", 'x')
        );
    }

    @Test
    public void throwsWhenParamsMoreThanFormatArgs() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Logger.format("String %s Number %d Char %c", "hey", 1, 'x', 2)
        );
    }

    @Test
    public void checksLogLevel() throws Exception {
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
    public void usesStringAsLoggerName() {
        Logger.info("com.jcabi.log...why.not", "hello, %s!", "world!");
    }

    @Test
    public void findsArgsByPositions() {
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

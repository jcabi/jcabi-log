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

import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MulticolorLayout}.
 *
 * @since 0.1
 */
final class MulticolorLayoutTest {

    /**
     * Conversation pattern for test case.
     */
    private static final String CONV_PATTERN = "[%color{%p}] %color{%m}";

    @Test
    void transformsLoggingEventToText() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern(MulticolorLayoutTest.CONV_PATTERN);
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("hello").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '[\\u001B[2;37mDEBUG\\u001B[m] \\u001B[2;37mhello\\u001B[m'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo(
                "[\\u001B[2;37mDEBUG\\u001B[m] \\u001B[2;37mhello\\u001B[m"
            )
        );
    }

    @Test
    void overwriteDefaultColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern(MulticolorLayoutTest.CONV_PATTERN);
        layout.setLevels("INFO:2;10");
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.INFO).when(event).getLevel();
        Mockito.doReturn("change").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '[\\u001B[2;10mINFO\\u001B[m] \\u001B[2;10mchange\\u001B[m'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo(
                "[\\u001B[2;10mINFO\\u001B[m] \\u001B[2;10mchange\\u001B[m"
            )
        );
    }

    @Test
    void rendersCustomConstantColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern("%color-red{%p} %m");
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("foo").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '\\u001B[31mDEBUG\\u001B[m foo'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo("\\u001B[31mDEBUG\\u001B[m foo")
        );
    }

    @Test
    void overwriteCustomConstantColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern("%color-white{%p} %m");
        layout.setColors("white:10");
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("const").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '\\u001B[10mDEBUG\\u001B[m const'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo("\\u001B[10mDEBUG\\u001B[m const")
        );
    }

    @Test
    void rendersAnsiConstantColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern("%color-0;0;31{%p} %m");
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("bar").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '\\u001B[0;0;31mDEBUG\\u001B[m bar'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo("\\u001B[0;0;31mDEBUG\\u001B[m bar")
        );
    }

    @Test
    void throwsOnIllegalColorName() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
                final MulticolorLayout layout = new MulticolorLayout();
                layout.setConversionPattern("%color-oops{%p} %m");
                final LoggingEvent event = Mockito.mock(LoggingEvent.class);
                Mockito.doReturn(Level.DEBUG).when(event).getLevel();
                Mockito.doReturn("text").when(event).getRenderedMessage();
                layout.format(event);
            }
        );
    }

}

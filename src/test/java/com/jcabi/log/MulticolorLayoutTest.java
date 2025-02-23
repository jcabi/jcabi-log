/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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

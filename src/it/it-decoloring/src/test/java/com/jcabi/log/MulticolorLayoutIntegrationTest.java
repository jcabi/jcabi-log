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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Integration tests for MulticolorLayout. These have to
 * be run with maven-invoker-plugin because they set the system property
 * com.jcabi.log.coloring, which interferes with other tests (tests are run
 * by multiple threads at once)
 * @since 0.1
 */
final class MulticolorLayoutIntegrationTest {
    /**
     * Conversation pattern for test case.
     */
    private static final String CONV_PATTERN = "[%color{%p}] %color{%m}";

    /**
     * Property that dictates wheter the text should be coloured or not.
     */
    private static final String COLORING_PROPERTY = "com.jcabi.log.coloring";

    /**
     * MulticolorLayout disables default color
     * when -Dcom.jcabi.log.coloring=false.
     */
    @Test
    void disablesDefaultColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern(
            MulticolorLayoutIntegrationTest.CONV_PATTERN
        );
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("no color").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '[\\u001B[2;37mDEBUG\\u001B[m] \\u001B[2;37mno color\\u001B[m'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo(
                "[\\u001B[2;37mDEBUG\\u001B[m] \\u001B[2;37mno color\\u001B[m"
            )
        );
        System.getProperties().setProperty(
            MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
            Boolean.FALSE.toString()
        );
        try {
            MatcherAssert.assertThat(
                "should equal to '[DEBUG] no color'",
                StringEscapeUtils.escapeJava(layout.format(event)),
                Matchers.equalTo(
                    "[DEBUG] no color"
                )
            );
        } finally {
            System.getProperties().setProperty(
                MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
                Boolean.TRUE.toString()
            );
        }
    }

    /**
     * MulticolorLayout disables the color that overwrites the default one.
     */
    @Test
    void disablesOverridenDefaultColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern("[%color{%p}] %m");
        layout.setLevels("DEBUG:2;10");
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("no colour text").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '[DEBUG] no color'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo(
                "[\\u001B[2;10mDEBUG\\u001B[m] no colour text"
            )
        );
        System.getProperties().setProperty(
            MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
            Boolean.FALSE.toString()
        );
        try {
            MatcherAssert.assertThat(
                "should equal to '[DEBUG] no colour text'",
                StringEscapeUtils.escapeJava(layout.format(event)),
                Matchers.equalTo(
                    "[DEBUG] no colour text"
                )
            );
        } finally {
            System.getProperties().setProperty(
                MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
                Boolean.TRUE.toString()
            );
        }
    }

    /**
     * MulticolorLayout disables constant color
     * when -Dcom.jcabi.log.coloring=false.
     */
    @Test
    void disablesConstantColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern("[%color-blue{%p}] %color-blue{%m}");
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("no color text").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '[\\u001B[34mDEBUG\\u001B[m] \\u001B[34mno color text\\u001B[m'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo(
                "[\\u001B[34mDEBUG\\u001B[m] \\u001B[34mno color text\\u001B[m"
            )
        );
        System.getProperties().setProperty(
            MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
            Boolean.FALSE.toString()
        );
        try {
            MatcherAssert.assertThat(
                "should equal to '[DEBUG] no color text'",
                StringEscapeUtils.escapeJava(layout.format(event)),
                Matchers.equalTo(
                    "[DEBUG] no color text"
                )
            );
        } finally {
            System.getProperties().setProperty(
                MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
                Boolean.TRUE.toString()
            );
        }
    }

    /**
     * MulticolorLayout disables the color that overwrites the constant one.
     */
    @Test
    void disablesOverridenConstantColor() {
        final MulticolorLayout layout = new MulticolorLayout();
        layout.setConversionPattern("[%color-red{%p}] %color-red{%m}");
        layout.setColors("red:12");
        final LoggingEvent event = Mockito.mock(LoggingEvent.class);
        Mockito.doReturn(Level.DEBUG).when(event).getLevel();
        Mockito.doReturn("test").when(event).getRenderedMessage();
        MatcherAssert.assertThat(
            "should equal to '[\\u001B[12mDEBUG\\u001B[m] \\u001B[12mtest\\u001B[m'",
            StringEscapeUtils.escapeJava(layout.format(event)),
            Matchers.equalTo(
                "[\\u001B[12mDEBUG\\u001B[m] \\u001B[12mtest\\u001B[m"
            )
        );
        System.getProperties().setProperty(
            MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
            Boolean.FALSE.toString()
        );
        try {
            MatcherAssert.assertThat(
                "should equals to '[DEBUG] test'",
                StringEscapeUtils.escapeJava(layout.format(event)),
                Matchers.equalTo(
                    "[DEBUG] test"
                )
            );
        } finally {
            System.getProperties().setProperty(
                MulticolorLayoutIntegrationTest.COLORING_PROPERTY,
                Boolean.TRUE.toString()
            );
        }
    }

}

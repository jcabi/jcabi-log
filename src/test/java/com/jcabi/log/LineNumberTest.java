/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for %L pattern.
 * If you change this class, you have to care about line number
 * in "com.jcabi.log.LineNumberTest:72"
 * @since 1.18
 */
final class LineNumberTest {

    /**
     * Conversation pattern for test case.
     */
    private static final String CONV_PATTERN = "%c:%L";

    @Test
    void testLineNumber() throws Exception {
        final PatternLayout layout = new PatternLayout();
        layout.setConversionPattern(LineNumberTest.CONV_PATTERN);
        final org.apache.log4j.Logger root = LogManager.getRootLogger();
        final Level level = root.getLevel();
        root.setLevel(Level.INFO);
        final StringWriter writer = new StringWriter();
        final Appender appender = new WriterAppender(layout, writer);
        root.addAppender(appender);
        try {
            Logger.info(this, "Test");
            TimeUnit.MILLISECONDS.sleep(1L);
            MatcherAssert.assertThat(
                "should contains a 'com.jcabi.log.LineNumberTest:216'",
                writer.toString(),
                Matchers.containsString(
                    "com.jcabi.log.LineNumberTest:216"
                )
            );
        } finally {
            root.removeAppender(appender);
            root.setLevel(level);
        }
    }
}

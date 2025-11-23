/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import org.apache.log4j.Level;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SupplierLogger}.
 * @since 0.18
 * @todo #100:30min Some tests here are ignored since they conflict
 *  in multi-threading run. I don't know exactly how to fix them,
 *  but we need to fix and remove the "Ignore" annotations.
 */
@SuppressWarnings("PMD.MoreThanOneLogger")
final class SupplierLoggerTest {

    @Test
    void debugIsDisabled() {
        final String name = "nodebug";
        final String appender = "nodebugapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.ERROR
        );
        SupplierLogger.debug(
            name, "Debug disabled: %s",
            (Supplier<String>) () -> "test1"
        );
        MatcherAssert.assertThat(
            "debug should be disabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.emptyString()
        );
    }

    @Test
    @Disabled
    void debugIsEnabled() {
        final String name = "debugen";
        final String appender = "debugapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.DEBUG
        );
        final String text = "test2";
        SupplierLogger.debug(
            name, "Debug enabled: %s",
            (Supplier<String>) () -> text
        );
        MatcherAssert.assertThat(
            "debug should be enabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.containsString(text)
        );
    }

    @Test
    void traceIsDisabled() {
        final String name = "notrace";
        final String appender = "notraceapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.ERROR
        );
        SupplierLogger.trace(
            name, "Trace disabled: %s",
            (Supplier<String>) () -> "test3"
        );
        MatcherAssert.assertThat(
            "trace should be disabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.emptyString()
        );
    }

    @Test
    void traceIsEnabled() {
        final String name = "enabledtrace";
        final String appender = "traceapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.TRACE
        );
        final String text = "text4";
        SupplierLogger.trace(
            name, "Trace enabled: %s",
            (Supplier<String>) () -> text
        );
        MatcherAssert.assertThat(
            "trace should be enabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.containsString(text)
        );
    }

    @Test
    void warnIsDisabled() {
        final String name = "nowarn";
        final String appender = "nowarnapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.ERROR
        );
        SupplierLogger.warn(
            name, "Warn disabled: %s",
            (Supplier<String>) () -> "test5"
        );
        MatcherAssert.assertThat(
            "warn should be disabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.emptyString()
        );
    }

    @Test
    @Disabled
    void warnIsEnabled() {
        final String name = "enwarn";
        final String appender = "warnapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.WARN
        );
        final String text = "test6";
        SupplierLogger.warn(
            name, "Warn enabled: %s",
            (Supplier<String>) () -> text
        );
        MatcherAssert.assertThat(
            "warn should be enabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.containsString(text)
        );
    }

    @Test
    void infoIsDisabled() {
        final String name = "noinfo";
        final String appender = "noinfoapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.WARN
        );
        SupplierLogger.info(
            name, "Info disabled: %s",
            (Supplier<String>) () -> "test7"
        );
        MatcherAssert.assertThat(
            "info should be disabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.emptyString()
        );
    }

    @Test
    @Disabled
    void infoIsEnabled() {
        final String name = "withinfo";
        final String appender = "infoapp";
        final org.apache.log4j.Logger logger = this.loggerForTest(
            name, appender, Level.INFO
        );
        final String text = "text8";
        SupplierLogger.info(
            name, "Info enabled: %s",
            (Supplier<String>) () -> text
        );
        MatcherAssert.assertThat(
            "info should be enabled",
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.containsString(text)
        );
    }

    /**
     * Builds a logger for each test method.
     * @param name Logger's name
     * @param appender Appender's name
     * @param level Logging level
     * @return Logger for test
     */
    private org.apache.log4j.Logger loggerForTest(
        final String name, final String appender, final Level level) {
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(name);
        final UnitTestAppender app = new UnitTestAppender(appender);
        app.activateOptions();
        logger.addAppender(app);
        logger.setLevel(level);
        return logger;
    }

}

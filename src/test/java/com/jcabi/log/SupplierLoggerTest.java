/**
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

import org.apache.log4j.Level;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link SupplierLogger}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.18
 */
@SuppressWarnings("PMD.MoreThanOneLogger")
public final class SupplierLoggerTest {

    /**
     * SupplierLogger can tell if debug is disabled and the message is not
     * logged enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void debugIsDisabled() throws Exception {
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
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with debug level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void debugIsEnabled() throws Exception {
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
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.containsString(text)
        );
    }

    /**
     * SupplierLogger can tell if trace is disabled and the message is not
     * logged enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void traceIsDisabled() throws Exception {
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
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with trace level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void traceIsEnabled() throws Exception {
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
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.containsString(text)
        );
    }

    /**
     * SupplierLogger can tell if warn is disabled and the message is not
     * logged enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void warnIsDisabled() throws Exception {
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
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with warn level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void warnIsEnabled() throws Exception {
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
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.containsString(text)
        );
    }

    /**
     * SupplierLogger can tell if info is disabled and the message is not
     * logged.
     * @throws Exception If something goes wrong
     */
    @Test
    public void infoIsDisabled() throws Exception {
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
            ((UnitTestAppender) logger.getAppender(appender)).output(),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with info level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void infoIsEnabled() throws Exception {
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
    public org.apache.log4j.Logger loggerForTest(
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

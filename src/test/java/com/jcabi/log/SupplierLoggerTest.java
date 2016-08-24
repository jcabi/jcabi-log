/**
 * Copyright (c) 2012-2015, jcabi.com
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

import java.io.IOException;
import org.apache.log4j.Level;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
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
     * The name of the appender defined for this unit test class in
     * log4j.properties.
     */
    private static final String APPENDER = "sltest";

    /**
     * SupplierLogger does not log a message with DEBUG level if debug is not
     * enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void debugIsDisabled() throws Exception {
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.ERROR);
        Logger.withSupplier().debug(
            this.getClass().getSimpleName(), "Debug disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test1";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger logs a message with DEBUG level if debug is enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void debugIsEnabled() throws Exception {
        final String text = "test2";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.DEBUG);
        Logger.withSupplier().debug(
            this.getClass().getSimpleName(), "Debug enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(appender.output().toByteArray()),
            Matchers.containsString(text)
        );
    }

    /**
     * SupplierLogger does not log a message with TRACE level if debug is not
     * enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void traceIsDisabled() throws Exception {
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.OFF);
        Logger.withSupplier().trace(
            this.getClass().getSimpleName(), "Trace disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test3";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger logs a message with TRACE level if debug is enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void traceIsEnabled() throws Exception {
        final String text = "text4";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.TRACE);
        Logger.withSupplier().trace(
            this.getClass().getSimpleName(), "Trace enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(appender.output().toByteArray()),
            Matchers.containsString(text)
        );
    }

    /**
     * SupplierLogger does not log a message with WARN level if debug is not
     * enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void warnIsDisabled() throws Exception {
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.OFF);
        Logger.withSupplier().warn(
            this.getClass().getSimpleName(), "Warn disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test5";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger logs a message with WARN level if debug is enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void warnIsEnabled() throws Exception {
        final String text = "test6";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.WARN);
        Logger.withSupplier().warn(
            this.getClass().getSimpleName(), "Warn enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(appender.output().toByteArray()),
            Matchers.containsString(text)
        );
    }

    /**
     * SupplierLogger does not log a message with INFO level if debug is not
     * enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void infoIsDisabled() throws Exception {
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.OFF);
        Logger.withSupplier().info(
            this.getClass().getSimpleName(), "Info disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test7";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger logs a message with INFO level if debug is enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void infoIsEnabled() throws Exception {
        final String text = "text8";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(this.getClass().getSimpleName());
        logger.setLevel(Level.INFO);
        Logger.withSupplier().info(
            this.getClass().getSimpleName(), "Info enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender(SupplierLoggerTest.APPENDER);
        MatcherAssert.assertThat(
            new String(appender.output().toByteArray()),
            Matchers.containsString(text)
        );
    }

    /**
     * Cleans the appender's OutputStream after each test.
     * This method is guaranteed to run even if an exception is thrown during
     * a test run.
     * @throws IOException If something goes wrong
     */
    @After
    public void cleanLogs() throws IOException {
        final UnitTestAppender appender = (UnitTestAppender) org.apache.log4j
            .Logger.getLogger(
                this.getClass().getSimpleName()
            ).getAppender(SupplierLoggerTest.APPENDER);
        appender.output().reset();
    }

}

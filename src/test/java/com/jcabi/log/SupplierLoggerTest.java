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

import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
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
     * Layout for logging.
     */
    private static final String LAYOUT = "org.apache.log4j.PatternLayout";

    /**
     * Pattern for logging.
     */
    private static final String PATTERN = "%d %c{1} - %m%n";

    /**
     * Appender for logging.
     */
    private static final String APPENDER = "com.jcabi.log.UnitTestAppender";

    /**
     * SupplierLogger does not log a message with DEBUG level if debug is not
     * enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void debugIsDisabled() throws Exception {
        final String loggerName = "nodebug";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.nodebug", "INFO, nodebugapp");
        prop.setProperty(
            "log4j.appender.nodebugapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.nodebugapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.nodebugapp.layout.ConversionPattern",
            SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.ERROR);
        Logger.withSupplier().debug(
            loggerName, "Debug disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test1";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender("nodebugapp");
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
        final String loggerName = "debugen";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.debugen", "INFO, debugapp");
        prop.setProperty(
            "log4j.appender.debugapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.debugapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.debugapp.layout.ConversionPattern",
            SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final String text = "test2";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.DEBUG);
        Logger.withSupplier().debug(
            loggerName, "Debug enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender("debugapp");
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
        final String loggerName = "notrace";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.notrace", "INFO, notraceapp");
        prop.setProperty(
            "log4j.appender.notraceapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.notraceapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.notraceapp.layout.ConversionPattern",
            SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.OFF);
        Logger.withSupplier().trace(
            loggerName, "Trace disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test3";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender("notraceapp");
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
        final String loggerName = "enabledtrace";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.enabledtrace", "INFO, traceapp");
        prop.setProperty(
            "log4j.appender.traceapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.traceapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.traceapp.layout.ConversionPattern",
            SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final String text = "text4";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.TRACE);
        Logger.withSupplier().trace(
            loggerName, "Trace enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender("traceapp");
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
        final String loggerName = "nowarn";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.nowarn", "INFO, nowarnapp");
        prop.setProperty(
            "log4j.appender.nowarnapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.nowarnapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.nowarnapp.layout.ConversionPattern",
            SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.OFF);
        Logger.withSupplier().warn(
            loggerName, "Warn disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test5";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender("nowarnapp");
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
        final String loggerName = "enwarn";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.enwarn", "INFO, warnapp");
        prop.setProperty(
            "log4j.appender.warnapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.warnapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.warnapp.layout.ConversionPattern",
            SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final String text = "test6";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.WARN);
        Logger.withSupplier().warn(
            loggerName, "Warn enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender("warnapp");
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
        final String loggerName = "noinfo";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.noinfo", "INFO, noinfoapp");
        prop.setProperty(
            "log4j.appender.noinfoapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.noinfoapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.noinfoapp.layout.ConversionPattern",
             SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.OFF);
        Logger.withSupplier().info(
            loggerName, "Info disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test7";
                }
            }
        );
        final UnitTestAppender app = (UnitTestAppender) logger
            .getAppender("noinfoapp");
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
        final String loggerName = "withinfo";
        final Properties prop = new Properties();
        prop.setProperty("log4j.logger.withinfo", "INFO, infoapp");
        prop.setProperty(
            "log4j.appender.infoapp", SupplierLoggerTest.APPENDER
        );
        prop.setProperty(
            "log4j.appender.infoapp.layout",
            SupplierLoggerTest.LAYOUT
        );
        prop.setProperty(
            "log4j.appender.infoapp.layout.ConversionPattern",
            SupplierLoggerTest.PATTERN
        );
        PropertyConfigurator.configure(prop);
        final String text = "text8";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        logger.setLevel(Level.INFO);
        Logger.withSupplier().info(
            loggerName, "Info enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        final UnitTestAppender appender = (UnitTestAppender) logger
            .getAppender("infoapp");
        MatcherAssert.assertThat(
            new String(appender.output().toByteArray()),
            Matchers.containsString(text)
        );
    }

}

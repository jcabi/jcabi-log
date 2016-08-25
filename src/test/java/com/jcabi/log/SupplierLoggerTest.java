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
        final String loggerName = "nodebug";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("nodebugapp");
        app.activateOptions();
        logger.addAppender(app);
        logger.setLevel(Level.ERROR);
        Logger.withSupplier().debug(
            loggerName, "Debug disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test1";
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with debug level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void debugIsEnabled() throws Exception {
        final String loggerName = "debugen";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("debugapp");
        app.activateOptions();
        logger.addAppender(app);
        final String text = "test2";
        logger.setLevel(Level.DEBUG);
        Logger.withSupplier().debug(
            loggerName, "Debug enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
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
        final String loggerName = "notrace";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("notraceapp");
        app.activateOptions();
        logger.addAppender(app);
        logger.setLevel(Level.OFF);
        Logger.withSupplier().trace(
            loggerName, "Trace disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test3";
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with trace level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void traceIsEnabled() throws Exception {
        final String loggerName = "enabledtrace";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("traceapp");
        app.activateOptions();
        logger.addAppender(app);
        final String text = "text4";
        logger.setLevel(Level.TRACE);
        Logger.withSupplier().trace(
            loggerName, "Trace enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
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
        final String loggerName = "nowarn";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("nowarnapp");
        app.activateOptions();
        logger.addAppender(app);
        logger.setLevel(Level.OFF);
        Logger.withSupplier().warn(
            loggerName, "Warn disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test5";
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with warn level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void warnIsEnabled() throws Exception {
        final String loggerName = "enwarn";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("warnapp");
        app.activateOptions();
        logger.addAppender(app);
        final String text = "test6";
        logger.setLevel(Level.WARN);
        Logger.withSupplier().warn(
            loggerName, "Warn enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.containsString(text)
        );
    }

    /**
     * SupplierLogger can tell if info is disabled and the message is not
     * logged enabled.
     * @throws Exception If something goes wrong
     */
    @Test
    public void infoIsDisabled() throws Exception {
        final String loggerName = "noinfo";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("noinfoapp");
        app.activateOptions();
        logger.addAppender(app);
        logger.setLevel(Level.OFF);
        Logger.withSupplier().info(
            loggerName, "Info disabled: %s",
            new Supplier<String>() {
                public String get() {
                    return "test7";
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.isEmptyString()
        );
    }

    /**
     * SupplierLogger can log a message with info level.
     * @throws Exception If something goes wrong
     */
    @Test
    public void infoIsEnabled() throws Exception {
        final String loggerName = "withinfo";
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(loggerName);
        final UnitTestAppender app = new UnitTestAppender();
        app.setName("infoapp");
        app.activateOptions();
        logger.addAppender(app);
        final String text = "text8";
        logger.setLevel(Level.INFO);
        Logger.withSupplier().info(
            loggerName, "Info enabled: %s",
            new Supplier<String>() {
                public String get() {
                    return text;
                }
            }
        );
        MatcherAssert.assertThat(
            new String(app.output().toByteArray()),
            Matchers.containsString(text)
        );
    }

}

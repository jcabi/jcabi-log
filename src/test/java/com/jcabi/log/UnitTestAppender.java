/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.log;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * Log4j appender for unit tests. Normally, we could use
 * <a href="http://projects.lidalia.org.uk/slf4j-test/">slf4j-test</a>, but we
 * have log4j in the classpath anyway, for {@link MulticolorLayout}.
 * @since 0.18
 */
final class UnitTestAppender extends WriterAppender {

    /**
     * OutputStream where this Appender writes.
     */
    private final transient ByteArrayOutputStream logs;

    /**
     * Ctor.
     * @param name The appender's name
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    UnitTestAppender(final String name) {
        super();
        this.setName(name);
        this.logs = new ByteArrayOutputStream();
    }

    /**
     * Prepares the appender for use.
     */
    public void activateOptions() {
        this.setWriter(this.createWriter(this.logs));
        this.setLayout(new PatternLayout("%d %c{1} - %m%n"));
        super.activateOptions();
    }

    /**
     * Return the logged messages.
     * @return String logs
     */
    public String output() {
        return new String(this.logs.toByteArray(), StandardCharsets.UTF_8);
    }

}

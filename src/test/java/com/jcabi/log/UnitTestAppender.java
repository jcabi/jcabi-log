/*
 * Copyright (c) 2012-2022, jcabi.com
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
    public UnitTestAppender(final String name) {
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

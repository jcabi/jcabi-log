/*
 * Copyright (c) 2012-2024, jcabi.com
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

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Formattable;
import java.util.Formatter;

/**
 * Decorates File.
 *
 * @since 0.1
 */
final class FileDecor implements Formattable {

    /**
     * The path.
     */
    private final transient Object path;

    /**
     * Public ctor.
     * @param file The file
     */
    FileDecor(final Object file) {
        this.path = file;
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        final StringWriter writer = new StringWriter();
        if (this.path == null) {
            writer.write("NULL");
        } else {
            final Path self = Paths.get(this.path.toString()).toAbsolutePath();
            final Path root = Paths.get("").toAbsolutePath();
            Path rlt;
            try {
                rlt = root.relativize(self);
            } catch (final IllegalArgumentException ex) {
                rlt = self;
            }
            String rel = rlt.toString();
            if (rel.startsWith("..")) {
                rel = self.toString();
            }
            if (rel.isEmpty()) {
                rel = "./";
            }
            writer.write(rel);
        }
        formatter.format("%s", writer);
    }

}

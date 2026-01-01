/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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

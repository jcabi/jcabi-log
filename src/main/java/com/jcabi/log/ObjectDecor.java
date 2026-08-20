/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.Formatter;

/**
 * Format internal structure of an object.
 * @since 0.1
 */
final class ObjectDecor implements Formattable {

    /**
     * The object to work with.
     */
    private final transient Object object;

    /**
     * Public ctor.
     * @param obj The object to format
     */
    ObjectDecor(final Object obj) {
        this.object = obj;
    }

    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.object == null) {
            formatter.format("NULL");
        } else if (this.object.getClass().isArray()) {
            formatter.format(
                new ArrayFormatAction((Object[]) this.object).run()
            );
        } else {
            formatter.format(
                new ObjectContentsFormatAction(this.object).run()
            );
        }
    }
}

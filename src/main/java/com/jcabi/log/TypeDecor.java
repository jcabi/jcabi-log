/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.Formatter;

/**
 * Decorator of a type.
 *
 * <p>For example:
 *
 * <pre>
 * public void func(Object input) {
 *   Logger.debug("Input of type %[type]s provided", input);
 * }
 * </pre>
 *
 * @since 0.1
 */
final class TypeDecor implements Formattable {

    /**
     * The object.
     */
    private final transient Object object;

    /**
     * Public ctor.
     * @param obj The object
     */
    TypeDecor(final Object obj) {
        this.object = obj;
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.object == null) {
            formatter.format("NULL");
        } else {
            formatter.format("%s", this.object.getClass().getName());
        }
    }

}

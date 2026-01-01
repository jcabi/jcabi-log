/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Arrays;
import java.util.Collection;
import java.util.Formattable;
import java.util.Formatter;

/**
 * Format list.
 * @since 0.1
 */
final class ListDecor implements Formattable {

    /**
     * The list.
     */
    private final transient Collection<?> list;

    /**
     * Public ctor.
     * @param obj The object to format
     * @throws DecorException If some problem with it
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    ListDecor(final Object obj) throws DecorException {
        if (obj == null || obj instanceof Collection) {
            this.list = Collection.class.cast(obj);
        } else if (obj instanceof Object[]) {
            this.list = Arrays.asList((Object[]) obj);
        } else {
            throw new DecorException(
                String.format(
                    "Collection or array required, while %s provided",
                    obj.getClass().getName()
                )
            );
        }
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        final StringBuilder builder = new StringBuilder(0);
        builder.append('[');
        if (this.list == null) {
            builder.append("NULL");
        } else {
            boolean first = true;
            for (final Object item : this.list) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append(String.format("\"%s\"", item));
                first = false;
            }
        }
        builder.append(']');
        formatter.format("%s", builder);
    }

}

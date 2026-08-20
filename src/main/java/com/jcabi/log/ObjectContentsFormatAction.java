/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

/**
 * {@link PrivilegedAction} for obtaining object contents.
 * @since 0.1
 */
final class ObjectContentsFormatAction
    implements PrivilegedAction<String> {

    /**
     * Object to format.
     */
    private final transient Object object;

    /**
     * Constructor.
     * @param obj Object to format
     */
    ObjectContentsFormatAction(final Object obj) {
        this.object = obj;
    }

    @Override
    public String run() {
        final StringBuilder builder = new StringBuilder("{");
        for (final Field field
            : this.object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                builder.append(
                    String.format(
                        "%s: \"%s\"",
                        field.getName(),
                        field.get(this.object)
                    )
                );
            } catch (final IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
            builder.append(", ");
        }
        builder.replace(builder.length() - 2, builder.length(), "}");
        return builder.toString();
    }
}

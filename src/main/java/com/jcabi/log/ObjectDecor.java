/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.util.Arrays;
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

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        if (this.object == null) {
            formatter.format("NULL");
        } else if (this.object.getClass().isArray()) {
            formatter.format(
                new ObjectDecor.ArrayFormatAction((Object[]) this.object).run()
            );
        } else {
            final String output =
                new ObjectDecor.ObjectContentsFormatAction(this.object).run();
            formatter.format(output);
        }
    }

    /**
     * {@link PrivilegedAction} for obtaining array contents.
     *
     * @since 0.1
     */
    private static final class ArrayFormatAction
        implements PrivilegedAction<String>  {
        /**
         * Array to format.
         */
        private final transient Object[] array;

        /**
         * Constructor.
         * @param arr Array to format
         */
        ArrayFormatAction(final Object... arr) {
            this.array = Arrays.copyOf(arr, arr.length);
        }

        @Override
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        public String run() {
            final StringBuilder builder = new StringBuilder("[");
            try (Formatter formatter = new Formatter(builder)) {
                for (final Object obj : this.array) {
                    new ObjectDecor(obj).formatTo(formatter, 0, 0, 0);
                    // @checkstyle MultipleStringLiteralsCheck (1 line)
                    builder.append(", ");
                }
            }
            builder.replace(builder.length() - 2, builder.length(), "]");
            return builder.toString();
        }
    }

    /**
     * {@link PrivilegedAction} for obtaining object contents.
     *
     * @since 0.1
     */
    private static final class ObjectContentsFormatAction
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
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                builder.append(", ");
            }
            builder.replace(builder.length() - 2, builder.length(), "}");
            return builder.toString();
        }
    }
}

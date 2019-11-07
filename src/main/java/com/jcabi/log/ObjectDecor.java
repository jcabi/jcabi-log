/**
 * Copyright (c) 2012-2017, jcabi.com
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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Formattable;
import java.util.Formatter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Format internal structure of an object.
 * @author Marina Kosenko (marina.kosenko@gmail.com)
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@ToString
@EqualsAndHashCode(of = "object")
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
                AccessController.doPrivileged(
                    new ArrayFormatAction((Object[]) this.object)
                )
            );
        } else {
            final String output =
                AccessController.doPrivileged(
                    new ObjectDecor.ObjectContentsFormatAction(this.object)
                );
            formatter.format(output);
        }
    }

    /**
     * {@link PrivilegedAction} for obtaining array contents.
     * @author Aleksey Popov (alopen@yandex.ru)
     * @version $Id$
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
            final Formatter formatter = new Formatter(builder);
            for (final Object obj : this.array) {
                new ObjectDecor(obj).formatTo(formatter, 0, 0, 0);
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                builder.append(", ");
            }
            builder.replace(builder.length() - 2, builder.length(), "]");
            return builder.toString();
        }
    }

    /**
     * {@link PrivilegedAction} for obtaining object contents.
     * @author Marina Kosenko (marina.kosenko@gmail.com)
     * @author Yegor Bugayenko (yegor256@gmail.com)
     * @version $Id$
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
        };
    }
}

package com.jcabi.log;

import com.jcabi.aspects.Immutable;
import java.security.PrivilegedAction;
import java.util.Formatter;

/**
 * {@link PrivilegedAction} for obtaining array contents.
 */
@Immutable
final class ArrayContentsFormatAction
    implements PrivilegedAction<String>  {
    private final Object[] array;

    public ArrayContentsFormatAction(final Object[] arr) {
        this.array = arr;
    }

    @Override
    public String run() {
        final StringBuilder builder = new StringBuilder("[");
        final Formatter formatter = new Formatter(builder);
        for (int index = 0; index < array.length; index++) {
            new ObjectDecor(array[index]).formatTo(formatter, 0, 0, 0);
            if (index < array.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(']');
        return builder.toString();
    }
}

package com.jcabi.log;

import com.jcabi.aspects.Immutable;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;

/**
 * {@link PrivilegedAction} for obtaining object contents.
 */
@Immutable
final class ObjectContentsFormatAction
    implements PrivilegedAction<String> {
    private final Object object;

    public ObjectContentsFormatAction(final Object obj) {
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
    };
}

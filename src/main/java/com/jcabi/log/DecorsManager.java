/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Formattable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manager of all decors.
 *
 * @since 0.1
 */
final class DecorsManager {

    /**
     * Storage of all found decors.
     */
    private static final ConcurrentMap<String, Class<? extends Formattable>> DECORS =
        new ConcurrentHashMap<>(0);

    static {
        DecorsManager.DECORS.put("file", FileDecor.class);
        DecorsManager.DECORS.put("dom", DomDecor.class);
        DecorsManager.DECORS.put("exception", ExceptionDecor.class);
        DecorsManager.DECORS.put("list", ListDecor.class);
        DecorsManager.DECORS.put("ms", MsDecor.class);
        DecorsManager.DECORS.put("nano", NanoDecor.class);
        DecorsManager.DECORS.put("object", ObjectDecor.class);
        DecorsManager.DECORS.put("size", SizeDecor.class);
        DecorsManager.DECORS.put("secret", SecretDecor.class);
        DecorsManager.DECORS.put("text", TextDecor.class);
        DecorsManager.DECORS.put("type", TypeDecor.class);
    }

    /**
     * Private ctor.
     */
    private DecorsManager() {
        // empty
    }

    /**
     * Get decor by key.
     * @param key Key for the formatter to be used to fmt the arguments
     * @param arg The arbument to supply
     * @return The decor
     * @throws DecorException If some problem
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Formattable decor(final String key, final Object arg)
        throws DecorException {
        final Class<? extends Formattable> type = DecorsManager.find(key);
        final Formattable decor;
        try {
            decor = (Formattable) DecorsManager.ctor(type).newInstance(arg);
        } catch (final InstantiationException ex) {
            throw new DecorException(
                ex,
                "Can't instantiate %s(%s)",
                type.getName(),
                arg.getClass().getName()
            );
        } catch (final IllegalAccessException ex) {
            throw new DecorException(
                ex,
                "Can't access %s(%s)",
                type.getName(),
                arg.getClass().getName()
            );
        } catch (final InvocationTargetException ex) {
            throw new DecorException(
                ex,
                "Can't invoke %s(%s)",
                type.getName(),
                arg.getClass().getName()
            );
        }
        return decor;
    }

    /**
     * Find decor.
     * @param key Key for the formatter to be used to fmt the arguments
     * @return The type of decor found
     * @throws DecorException If some problem
     */
    @SuppressWarnings("unchecked")
    private static Class<? extends Formattable> find(final String key)
        throws DecorException {
        final Class<? extends Formattable> type;
        if (DecorsManager.DECORS.containsKey(key)) {
            type = DecorsManager.DECORS.get(key);
        } else {
            try {
                type = (Class<Formattable>) Class.forName(key);
            } catch (final ClassNotFoundException ex) {
                throw new DecorException(
                    ex,
                    "Decor '%s' not found and class can't be instantiated",
                    key
                );
            }
        }
        return type;
    }

    /**
     * Get ctor of the type.
     * @param type The type
     * @return The ctor
     * @throws DecorException If some problem
     */
    private static Constructor<?> ctor(final Class<? extends Formattable> type)
        throws DecorException {
        final Constructor<?>[] ctors = type.getDeclaredConstructors();
        if (ctors.length != 1) {
            throw new DecorException(
                "%s should have just one one-arg ctor, but there are %d",
                type.getName(),
                ctors.length
            );
        }
        final Constructor<?> ctor = ctors[0];
        if (ctor.getParameterTypes().length != 1) {
            throw new DecorException(
                "%s public ctor should have just once parameter",
                type.getName()
            );
        }
        return ctor;
    }

}

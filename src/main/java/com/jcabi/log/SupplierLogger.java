/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Logging methods which take {@link Supplier} arguments.
 * Used with Java 8 method referencing.
 * @since 0.18
 * @checkstyle HideUtilityClassConstructorCheck (500 lines)
 */
@SuppressWarnings({ "PMD.ProhibitPublicStaticMethods", "PMD.UseUtilityClass" })
final class SupplierLogger {

    /**
     * Log one message, with {@code TRACE} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments. Objects are going
     *  to be extracted from them and used for log message interpolation
     */
    public static void trace(
        final Object source, final String msg, final Supplier<?>... args) {
        if (Logger.isTraceEnabled(source)) {
            Logger.traceForced(source, msg, SupplierLogger.supplied(args));
        }
    }

    /**
     * Log one message, with {@code DEBUG} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments. Objects are going
     *  to be extracted from them and used for log message interpolation
     */
    public static void debug(
        final Object source, final String msg, final Supplier<?>... args) {
        if (Logger.isDebugEnabled(source)) {
            Logger.debugForced(source, msg, SupplierLogger.supplied(args));
        }
    }

    /**
     * Log one message, with {@code INFO} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments. Objects are going
     *  to be extracted from them and used for log message interpolation
     */
    public static void info(
        final Object source, final String msg, final Supplier<?>... args) {
        if (Logger.isInfoEnabled(source)) {
            Logger.infoForced(source, msg, SupplierLogger.supplied(args));
        }
    }

    /**
     * Log one message, with {@code WARN} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments. Objects are going
     *  to be extracted from them and used for log message interpolation
     */
    public static void warn(
        final Object source, final String msg, final Supplier<?>... args) {
        if (Logger.isWarnEnabled(source)) {
            Logger.warnForced(source, msg, SupplierLogger.supplied(args));
        }
    }

    /**
     * Return the results of the given suppliers.
     * @param args Suppliers
     * @return Object array
     */
    private static Object[] supplied(final Supplier<?>... args) {
        final Object[] supplied = new Object[args.length];
        for (int idx = 0; idx < supplied.length; ++idx) {
            supplied[idx] = args[idx].get();
        }
        return supplied;
    }
}

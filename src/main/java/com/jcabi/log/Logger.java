/*
 * Copyright (c) 2012-2025, jcabi.com
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import org.slf4j.LoggerFactory;

/**
 * Universal logger, and adapter between your app and SLF4J API.
 *
 * <p>Instead of relying
 * on some logging engine you can use this class, which transforms all
 * messages to SLF4J. This approach gives you a perfect decoupling of business
 * logic and logging mechanism. All methods in the class are called
 * statically, without the necessity to instantiate the class.
 *
 * <p>Use it like this in any class, and in any package:
 *
 * <pre> package com.example.XXX;
 * import com.jcabi.log.Logger;
 * public class MyClass {
 *   public void foo(Integer num) {
 *     Logger.info(this, "foo(%d) just called", num);
 *   }
 * }</pre>
 *
 * <p>Or statically (pay attention to {@code MyClass.class}):
 *
 * <pre> public class MyClass {
 *   public static void foo(Integer num) {
 *     Logger.info(MyClass.class, "foo(%d) just called", num);
 *   }
 * }</pre>
 *
 * <p>Exact binding between SLF4J and logging facility has to be
 * specified in {@code pom.xml} of your project (or in classpath directly).
 *
 * <p>For performance reasons in most cases before sending a
 * {@code TRACE} or {@code DEBUG} log message you may check whether this
 * logging level is enabled in the project, e.g.:
 *
 * <pre> //...
 * if (Logger.isTraceEnabled(this)) {
 *   Logger.trace(this, "#foo() called");
 * }
 * //...</pre>
 *
 * <p>There is only one reason to do so - if you want to save time spent on
 * preparing of the arguments. By default, such a call is made inside every
 * method of {@link Logger} class.
 *
 * @since 0.1
 */
@SuppressWarnings(
    {
        "PMD.TooManyMethods", "PMD.GodClass", "PMD.ProhibitPublicStaticMethods"
    }
)
public final class Logger {

    /**
     * Empty array of objects.
     */
    private static final Object[] EMPTY = {};

    /**
     * UTF-8.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * This is utility class.
     */
    private Logger() {
        // intentionally empty
    }

    /**
     * Format one string.
     * @param fmt The format
     * @param args List of arbitrary arguments
     * @return Formatted string
     */
    public static String format(final String fmt, final Object... args) {
        final String result;
        if (args.length == 0) {
            result = fmt;
        } else {
            final PreFormatter pre = new PreFormatter(fmt, args);
            result = String.format(pre.getFormat(), pre.getArguments());
        }
        return result;
    }

    /**
     * Protocol one message, with {@code TRACE} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged
     * @since 0.7.11
     */
    public static void trace(final Object source, final String msg) {
        Logger.trace(source, msg, Logger.EMPTY);
    }

    /**
     * Protocol one message, with {@code TRACE} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void trace(
        final Object source,
        final String msg, final Object... args
    ) {
        if (Logger.isTraceEnabled(source)) {
            Logger.traceForced(source, msg, args);
        }
    }

    /**
     * Protocol one message, with {@code TRACE} priority level
     * without internal checking whether {@code TRACE} level is enabled.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void traceForced(
        final Object source,
        final String msg, final Object... args
    ) {
        Logger.logger(source).trace(Logger.format(msg, args));
    }

    /**
     * Protocol one message, with {@code DEBUG} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @since 0.7.11
     */
    public static void debug(final Object source, final String msg) {
        Logger.debug(source, msg, Logger.EMPTY);
    }

    /**
     * Protocol one message, with {@code DEBUG} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void debug(
        final Object source,
        final String msg, final Object... args
    ) {
        if (Logger.isDebugEnabled(source)) {
            Logger.debugForced(source, msg, args);
        }
    }

    /**
     * Protocol one message, with {@code DEBUG} priority level
     * without internal checking whether {@code DEBUG} level is enabled.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void debugForced(
        final Object source,
        final String msg, final Object... args
    ) {
        Logger.logger(source).debug(Logger.format(msg, args));
    }

    /**
     * Protocol one message, with {@code INFO} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged
     * @since 0.7.11
     */
    public static void info(final Object source, final String msg) {
        Logger.info(source, msg, Logger.EMPTY);
    }

    /**
     * Protocol one message, with {@code INFO} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void info(
        final Object source,
        final String msg, final Object... args
    ) {
        if (Logger.isInfoEnabled(source)) {
            Logger.infoForced(source, msg, args);
        }
    }

    /**
     * Protocol one message, with {@code INFO} priority level
     * without internal checking whether {@code INFO} level is enabled.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void infoForced(
        final Object source, final String msg,
        final Object... args
    ) {
        Logger.logger(source).info(Logger.format(msg, args));
    }

    /**
     * Protocol one message, with {@code WARN} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged
     * @since 0.7.11
     */
    public static void warn(final Object source, final String msg) {
        Logger.warn(source, msg, Logger.EMPTY);
    }

    /**
     * Protocol one message, with {@code WARN} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void warn(
        final Object source,
        final String msg, final Object... args
    ) {
        if (Logger.isWarnEnabled(source)) {
            Logger.warnForced(source, msg, args);
        }
    }

    /**
     * Protocol one message, with {@code WARN} priority level
     * without internal checking whether {@code WARN} level is enabled.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void warnForced(
        final Object source,
        final String msg, final Object... args
    ) {
        Logger.logger(source).warn(Logger.format(msg, args));
    }

    /**
     * Protocol one message, with {@code ERROR} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged
     * @since 0.7.11
     */
    public static void error(final Object source, final String msg) {
        Logger.error(source, msg, Logger.EMPTY);
    }

    /**
     * Protocol one message, with {@code ERROR} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of arguments
     */
    public static void error(final Object source,
        final String msg, final Object... args) {
        Logger.logger(source).error(Logger.format(msg, args));
    }

    /**
     * Validates whether {@code TRACE} priority level is enabled for
     * this particular logger.
     * @param source The source of the logging operation
     * @return Is it enabled?
     */
    public static boolean isTraceEnabled(final Object source) {
        return Logger.logger(source).isTraceEnabled();
    }

    /**
     * Validates whether {@code DEBUG} priority level is enabled for
     * this particular logger.
     * @param source The source of the logging operation
     * @return Is it enabled?
     */
    public static boolean isDebugEnabled(final Object source) {
        return Logger.logger(source).isDebugEnabled();
    }

    /**
     * Validates whether {@code INFO} priority level is enabled for
     * this particular logger.
     * @param source The source of the logging operation
     * @return Is it enabled?
     * @since 0.5
     */
    public static boolean isInfoEnabled(final Object source) {
        return Logger.logger(source).isInfoEnabled();
    }

    /**
     * Validates whether {@code INFO} priority level is enabled for
     * this particular logger.
     * @param source The source of the logging operation
     * @return Is it enabled?
     * @since 0.5
     */
    public static boolean isWarnEnabled(final Object source) {
        return Logger.logger(source).isWarnEnabled();
    }

    /**
     * Is the given logging level enabled?
     * @param level The level of logging
     * @param source The source of the logging operation
     * @return Is it enabled?
     * @since 0.13
     */
    public static boolean isEnabled(final Level level, final Object source) {
        boolean enabled = false;
        if (level.equals(Level.SEVERE)) {
            enabled = Logger.logger(source).isErrorEnabled();
        } else if (level.equals(Level.WARNING)) {
            enabled = Logger.logger(source).isWarnEnabled();
        } else if (level.equals(Level.INFO) || level.equals(Level.CONFIG)) {
            enabled = Logger.logger(source).isInfoEnabled();
        } else if (level.equals(Level.FINE) || level.equals(Level.ALL)) {
            enabled = Logger.logger(source).isDebugEnabled();
        } else if (level.equals(Level.FINER) || level.equals(Level.FINEST)) {
            enabled = Logger.logger(source).isTraceEnabled();
        }
        return enabled;
    }

    /**
     * Log one line using the logging level specified.
     * @param level The level of logging
     * @param source The source of the logging operation
     * @param msg The text message to be logged
     * @param args Optional arguments for string formatting
     * @since 0.8
     * @checkstyle ParameterNumber (4 lines)
     */
    public static void log(final Level level, final Object source,
        final String msg, final Object... args) {
        if (level.equals(Level.SEVERE)) {
            Logger.error(source, msg, args);
        } else if (level.equals(Level.WARNING)) {
            Logger.warn(source, msg, args);
        } else if (level.equals(Level.INFO) || level.equals(Level.CONFIG)) {
            Logger.info(source, msg, args);
        } else if (level.equals(Level.FINE) || level.equals(Level.ALL)) {
            Logger.debug(source, msg, args);
        } else if (level.equals(Level.FINER) || level.equals(Level.FINEST)) {
            Logger.trace(source, msg, args);
        }
    }

    /**
     * Returns an {@link OutputStream}, which converts all incoming data
     * into logging lines (separated by {@code \x0A} in UTF-8).
     * @param level The level of logging
     * @param source The source of the logging operation
     * @return Output stream directly pointed to the logging facility
     * @see <a href="http://stackoverflow.com/questions/17258325">some discussion</a>
     * @since 0.8
     */
    public static OutputStream stream(final Level level, final Object source) {
        // @checkstyle AnonInnerLengthCheck (50 lines)
        return new OutputStream() {
            private final transient ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();
            @Override
            public void write(final int data) throws IOException {
                if (data == '\n') {
                    Logger.log(
                        level, source,
                        this.buffer.toString(Logger.UTF_8)
                    );
                    this.buffer.reset();
                } else if (data >= 0x20 && data <= 0x7f) {
                    this.buffer.write(data);
                } else {
                    this.buffer.write(
                        String.format(
                            "\\x%02x", data & 0xff
                        ).getBytes(Logger.UTF_8)
                    );
                }
            }
        };
    }

    /**
     * Log messages constructed from Suppliers.
     * It is more efficient to use method referencing because the method
     * won't be called unless the specified logging level is enabled.
     *
     * This saves you the effort of having to check if the level is enabled
     * before calling the logging method.
     * E.g.
     * <pre>
     *     if (Logger.isDebugEnabled(this)) {
     *         Logger.debug(this, "Some %s", calculate());
     *     }
     * </pre><br>
     * turns into <br>
     * <pre>
     *     Logger.withSupplier().debug(this, "Some %s", this::calculate());
     * </pre><br>
     * and the calculate() method won't be called unless the debug level is
     * active.
     *
     * @return Object containing methods for logging with Supplier-constructed
     *  messages
     */
    public static SupplierLogger withSupplier() {
        return new SupplierLogger();
    }

    /**
     * Set final static field in order to fix the %L log4j parameter.
     * @param field Field
     * @param value New value
     * @throws NoSuchFieldException If some problem
     * @throws IllegalAccessException If some problem
     * @checkstyle ThrowsCountCheck (4 lines)
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static void setFinalStatic(final Field field, final Object value)
        throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        final Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, value);
    }

    /**
     * Get the instance of the logger for this particular caller.
     * @param source Source of the logging operation
     * @return The instance of {@code Logger} class
     */
    private static org.slf4j.Logger logger(final Object source) {
        final org.slf4j.Logger logger;
        if (source instanceof Class) {
            logger = LoggerFactory.getLogger((Class<?>) source);
        } else if (source instanceof String) {
            logger = LoggerFactory.getLogger(String.class.cast(source));
        } else {
            logger = LoggerFactory.getLogger(source.getClass());
        }
        if ("org.slf4j.impl.Log4jLoggerAdapter"
            .equals(logger.getClass().getName())) {
            try {
                final Field fqcn = logger.getClass()
                    .getDeclaredField("FQCN");
                setFinalStatic(fqcn, Logger.class.getName());
            } catch (final NoSuchFieldException | IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return logger;
    }

}

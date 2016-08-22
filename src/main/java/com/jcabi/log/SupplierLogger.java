/**
 * Copyright (c) 2012-2015, jcabi.com
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

import com.jcabi.log.functional.Supplier;

/**
 * Logging methods which take {@link Supplier} arguments.
 * Used with Java 8 method referencing.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.18
 */
final class SupplierLogger {

    /**
     * Log one message, with {@code TRACE} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments
     */
    public void trace(
        final Object source,
        final String msg, final Supplier<?>... args
    ) {
        if (Logger.isTraceEnabled(source)) {
            Logger.traceForced(source, msg, this.supplied(args));
        }
    }

    /**
     * Log one message, with {@code DEBUG} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments
     */
    public void debug(
        final Object source,
        final String msg, final Supplier<?>... args
    ) {
        if (Logger.isDebugEnabled(source)) {
            Logger.debugForced(source, msg, this.supplied(args));
        }
    }

    /**
     * Log one message, with {@code INFO} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments
     */
    public void info(
        final Object source,
        final String msg, final Supplier<?>... args
    ) {
        if (Logger.isInfoEnabled(source)) {
            Logger.infoForced(source, msg, this.supplied(args));
        }
    }

    /**
     * Log one message, with {@code WARN} priority level.
     * @param source The source of the logging operation
     * @param msg The text message to be logged, with meta-tags
     * @param args List of {@link Supplier} arguments
     */
    public void warn(
        final Object source,
        final String msg, final Supplier<?>... args
    ) {
        if (Logger.isWarnEnabled(source)) {
            Logger.warnForced(source, msg, this.supplied(args));
        }
    }

    /**
     * Return the results of the given suppliers.
     * @param args Suppliers
     * @return Object array
     */
    private Object[] supplied(final Supplier<?>... args) {
        final Object[] supplied = new Object[args.length];
        for (int idx = 0; idx < supplied.length; ++idx) {
            supplied[idx] = args[idx].get();
        }
        return supplied;
    }
}

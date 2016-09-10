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

/**
 * Example application to help test {@link VerboseProcess}.
 * @author Dean Clark (dean.clark@gmail.com)
 * @version $Id$
 */
public final class VerboseProcessExample {

    /**
     * System output line 1.
     */
    static final String SYSOUT_1 = "sysout line 1";

    /**
     * System output line 2.
     */
    static final String SYSOUT_2 = "sysout line 2";

    /**
     * Error output line 1.
     */
    static final String SYSERR_1 = "syserr line 1";

    /**
     * Error output line 2.
     */
    static final String SYSERR_2 = "syserr line 2";

    /**
     * Exception to be thrown and caught.
     */
    static final String CAUGHT_ERR_MSG = "throw/catch me";

    /**
     * Exception to be thrown.
     */
    static final String THROWN_ERR_MSG = "just throw me";

    /**
     * Private constructor.
     */
    private VerboseProcessExample() { }

    /**
     * Instantiates instance of this class and calls primary method.
     * @param args Any args passed to main method
     */
    public static void main(final String[] args) {
        final VerboseProcessExample instance = new VerboseProcessExample();
        instance.doWork();
    }

    /**
     * Will log to standard output and error and then intentionally fail with a
     * stack trace.
     */
    public void doWork() {
        System.out.println(SYSOUT_1);
        System.err.println(SYSOUT_1);
        System.out.println(SYSOUT_2);
        System.err.println(SYSOUT_2);
        catchAndThrow();
    }

    /**
     * Call a method which will throw an exception. Then catch and re-throw it.
     */
    private static void catchAndThrow() {
        try {
            countdownAndThrow(2);
        } catch (final IllegalStateException ex) {
            throw new IllegalStateException(THROWN_ERR_MSG, ex);
        }
    }

    /**
     * Recursively loops and then throws an exception.
     * @param loops Times to loop
     */
    private static void countdownAndThrow(final int loops) {
        if (loops == 0) {
            throw new IllegalStateException(CAUGHT_ERR_MSG);
        }
        countdownAndThrow(loops - 1);
    }

}

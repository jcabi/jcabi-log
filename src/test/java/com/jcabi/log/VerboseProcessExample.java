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
 */
 public class VerboseProcessExample {

    /**
     * System output line 1.
     */
    protected static final String SYSOUT_1 = "sysout line 1";
    /**
     * System output line 2.
     */
    protected static final String SYSOUT_2 = "sysout line 2";
    /**
     * System output line 3.
     */
    protected static final String SYSOUT_3 = "sysout line 3";
    /**
     * System output line 4.
     */
    protected static final String SYSOUT_4 = "sysout line 4";
    /**
     * Error output line 1.
     */
    protected static final String SYSERR_1 = "syserr line 1";
    /**
     * Error output line 2.
     */
    protected static final String SYSERR_2 = "syserr line 2";
    /**
     * Error output line 3.
     */
    protected static final String SYSERR_3 = "syserr line 3";
    /**
     * Error output line 4.
     */
    protected static final String SYSERR_4 = "syserr line 4";
    /**
     * Exception to be thrown and caught.
     */
    protected static final String CAUGHT_ERR_MSG = "this error gets thrown" 
        + " and caught";
    /**
     * Exception to be thrown.
     */
    protected static final String THROWN_ERR_MSG = "this error was caused by" 
        + " the other one";

    /**
     * Will log to standard output and error and then intentionally fail with a 
     * stack trace.
     * @param args
     */
    public static void main(final String[] args) {
        System.out.println(SYSOUT_1);
        System.err.println(SYSOUT_1);
        System.out.println(SYSOUT_2);
        System.err.println(SYSOUT_2);
        System.out.println(SYSOUT_3);
        System.err.println(SYSOUT_3);
        System.out.println(SYSOUT_4);
        System.err.println(SYSOUT_4);
        catchAndThrow(20);
    }

    /**
     * Call a method which will throw an exception. Then catch and re-throw it.
     * @param i Times to loop
     */
    private static void catchAndThrow(final int i) {
        try {
            countdownAndThrow(i);
        } catch (final RuntimeException e) {
            throw new RuntimeException(THROWN_ERR_MSG, e);
        }
    }

    /**
     * Recursively loops i-times and then throws an exception.
     * @param i Times to loop
     * @throws Exception
     */
    private static void countdownAndThrow(final int i) throws RuntimeException {
        if (i == 0) {
            throw new RuntimeException(CAUGHT_ERR_MSG);
        }
        countdownAndThrow(i - 1);
    }

}

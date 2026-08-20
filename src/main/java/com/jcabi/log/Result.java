/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Class representing the result of a process.
 * @since 0.1
 */
public final class Result {

    /**
     * Returned code from the process.
     */
    private final transient int exit;

    /**
     * {@code stdout} from the process.
     */
    private final transient String out;

    /**
     * {@code stderr} from the process.
     */
    private final transient String err;

    /**
     * Result class constructor.
     * @param code The exit code
     * @param stdout The {@code stdout} from the process
     * @param stderr The {@code stderr} from the process
     */
    Result(final int code, final String stdout, final String stderr) {
        this.exit = code;
        this.out = stdout;
        this.err = stderr;
    }

    /**
     * Get {@code code} from the process.
     * @return Full {@code code} of the process
     */
    public int code() {
        return this.exit;
    }

    /**
     * Get {@code stdout} from the process.
     * @return Full {@code stdout} of the process
     */
    public String stdout() {
        return this.out;
    }

    /**
     * Get {@code stderr} from the process.
     * @return Full {@code stderr} of the process
     */
    public String stderr() {
        return this.err;
    }
}

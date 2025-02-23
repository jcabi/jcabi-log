/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Functional interface used as assignment target for Java8 lambda expressions
 * or method references. <b>Can be used for method referencing when the method
 * signature respects the following: returns something and takes no arguments.
 * </b>
 * @param <T> The type of results supplied by this supplier
 * @since 0.18
 */
public interface Supplier<T> {

    /**
     * Gets a result.
     * @return A result
     */
    T get();

}

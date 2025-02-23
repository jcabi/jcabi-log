/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

/**
 * Contract for a class that know how to format something.
 * @since 0.18
 */
interface Formatted {

    /**
     * Return something formatted.
     * @return Formatted version of something
     */
    String format();

}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.slf4j.impl;

import org.slf4j.helpers.NOPLogger;

/**
 * Fake adapter that looks like the one from slf4j-log4j12.
 *
 * <p>It has the same class name and the same {@code static final}
 * field {@code FQCN}, so {@link com.jcabi.log.Logger} treats it
 * like the real adapter.</p>
 *
 * @since 0.24.4
 */
public final class Log4jLoggerAdapter extends NOPLogger {

    /**
     * Fully qualified name of the calling class.
     */
    private static final String FQCN = Log4jLoggerAdapter.class.getName();

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 0x7D2645AB4F3E1C2DL;

    /**
     * Ctor.
     */
    public Log4jLoggerAdapter() {
        super();
    }

    @Override
    public String getName() {
        return Log4jLoggerAdapter.FQCN;
    }
}

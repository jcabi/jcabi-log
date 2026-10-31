/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * SLF4J provider that returns {@link Log4jLoggerAdapter} for every name.
 *
 * @since 0.24.4
 */
public final class FakeLog4jProvider implements SLF4JServiceProvider {

    /**
     * Ctor.
     */
    public FakeLog4jProvider() {
        // Nothing to initialize
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return name -> new Log4jLoggerAdapter();
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return new BasicMarkerFactory();
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return new NOPMDCAdapter();
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0.99";
    }

    @Override
    public void initialize() {
        this.getLoggerFactory();
    }
}

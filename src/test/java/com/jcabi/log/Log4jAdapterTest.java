/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * Test case for {@link Logger} bound to the Log4j 1.x adapter.
 *
 * @since 0.24.4
 */
final class Log4jAdapterTest {

    @Test
    void logsThroughLegacyAdapterWithoutFailure() throws Exception {
        try (
            URLClassLoader loader = new URLClassLoader(
                new URL[] {
                    Logger.class.getProtectionDomain().getCodeSource()
                        .getLocation(),
                    Log4jAdapterTest.class.getProtectionDomain()
                        .getCodeSource().getLocation(),
                    Paths.get(
                        Log4jAdapterTest.class.getResource("adapter").toURI()
                    ).toUri().toURL(),
                    LoggerFactory.class.getProtectionDomain().getCodeSource()
                        .getLocation(),
                },
                ClassLoader.getPlatformClassLoader()
            )
        ) {
            MatcherAssert.assertThat(
                "should log through the Log4j adapter without an exception",
                Callable.class.cast(
                    loader.loadClass(Greeting.class.getName())
                        .getDeclaredConstructor().newInstance()
                ).call(),
                Matchers.is("logged")
            );
        }
    }
}

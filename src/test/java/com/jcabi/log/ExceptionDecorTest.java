/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.IOException;
import java.util.Formattable;
import java.util.Formatter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;

/**
 * Test case for {@link ExceptionDecor}.
 * @since 0.1
 */
final class ExceptionDecorTest {

    @Test
    void convertsExceptionToText() throws Exception {
        final Formattable decor = new ExceptionDecor(new IOException("ouch!"));
        final Appendable dest = Mockito.mock(Appendable.class);
        try (Formatter fmt = new Formatter(dest)) {
            decor.formatTo(fmt, 0, 0, 0);
        }
        Mockito.verify(dest).append(
            MockitoHamcrest.argThat(
                Matchers.allOf(
                    Matchers.containsString(
                        "java.io.IOException: ouch!"
                    ),
                    Matchers.containsString(
                        "at com.jcabi.log.ExceptionDecorTest."
                    )
                )
            )
        );
    }

    @Test
    void convertsNullToText() throws Exception {
        final Formattable decor = new ExceptionDecor(null);
        final Appendable dest = Mockito.mock(Appendable.class);
        try (Formatter fmt = new Formatter(dest)) {
            decor.formatTo(fmt, 0, 0, 0);
        }
        Mockito.verify(dest).append("NULL");
    }

}

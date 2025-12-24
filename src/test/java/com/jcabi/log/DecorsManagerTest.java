/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DecorsManager}.
 * @since 0.1
 */
final class DecorsManagerTest {

    @Test
    void hasBuiltInDecors() throws Exception {
        MatcherAssert.assertThat(
            "should be an instance of NanoDecor.class",
            DecorsManager.decor("nano", 1L),
            Matchers.instanceOf(NanoDecor.class)
        );
    }

    @Test
    void throwsExceptionForAbsentDecor() {
        Assertions.assertThrows(
            DecorException.class,
            () -> DecorsManager.decor("non-existing-formatter", null)
        );
    }

}

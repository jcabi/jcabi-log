/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts items inside a string like K1:V1,K2:V2 - where K is for key and V
 * is for value - to a {@code Map} of string key and string value.
 * @since 0.18
 */
class ParseableInformation {

    /**
     * Information content to be parsed.
     */
    private final transient String content;

    /**
     * Constructor.
     * @param cont Content to be parsed
     */
    ParseableInformation(final String cont) {
        this.content = cont;
    }

    /**
     * Parse the information.
     * @return A {@link Map} with a key,value pair os strings
     */
    final Map<String, String> information() {
        final Map<String, String> parsed = new HashMap<>(0);
        try {
            for (final String item : this.items()) {
                final String[] values = item.split(":");
                parsed.put(values[0], values[1]);
            }
        } catch (final ArrayIndexOutOfBoundsException ex) {
            throw new IllegalStateException(
                String.format(
                    "Information is not using the pattern KEY1:VALUE,KEY2:VALUE %s",
                    this.content
                ),
                ex
            );
        }
        return parsed;
    }

    /**
     * Split the information using {@link ParseableInformation#SPLIT_ITEMS}
     * pattern.
     * @return An array of items
     */
    private String[] items() {
        return this.content.split(",");
    }
}

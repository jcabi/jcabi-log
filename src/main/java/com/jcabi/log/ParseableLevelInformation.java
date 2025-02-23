/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Level;

/**
 * Parse information like {@code ParseInformation} does, but increments with
 * some extra checks for {@code Level}s.
 * @since 0.18
 */
class ParseableLevelInformation  {

    /**
     * Information content to be parsed.
     */
    private final transient String content;

    /**
     * Construtor.
     * @param cont Content to be parsed
     */
    ParseableLevelInformation(final String cont) {
        this.content = cont;
    }

    /**
     * Parse the level information.
     * @return A {@link Map} with key,value pair of strings
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    public final Map<String, String> information() {
        final Map<String, String> parsed = new ParseableInformation(
            this.content
        ).information();
        final Map<String, String> converted = new HashMap<>(0);
        for (final Map.Entry<String, String> entry : parsed.entrySet()) {
            final String level = entry.getKey().toUpperCase(Locale.ENGLISH);
            if (Level.toLevel(level, null) == null) {
                throw new IllegalStateException(
                    String.format(Locale.ENGLISH, "Unknown level '%s'", level)
                );
            }
            converted.put(level, entry.getValue());
        }
        return converted;
    }

}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Store human readable color data.
 * @since 0.18
 */
public class Colors {

    /**
     * Default color name to ANSI code mapping.
     */
    private static final Map<String, String> DEFAULTS = Map.ofEntries(
        Map.entry("black", "30"),
        Map.entry("blue", "34"),
        Map.entry("cyan", "36"),
        Map.entry("green", "32"),
        Map.entry("magenta", "35"),
        Map.entry("red", "31"),
        Map.entry("yellow", "33"),
        Map.entry("white", "37")
    );

    /**
     * Colors with names.
     */
    private final transient ConcurrentMap<String, String> map =
        new ConcurrentHashMap<>(Colors.DEFAULTS);

    /**
     * Add color to color map.
     * @param key Key to add
     * @param value Value to add
     */
    public final void addColor(final String key, final String value) {
        this.map.put(key, value);
    }

    /**
     * Convert our text to ANSI color.
     * @param meta Meta text
     * @return ANSI color
     */
    public final String ansi(final String meta) {
        final String ansi;
        if (meta == null) {
            ansi = "?";
        } else if (meta.matches("[a-z]+")) {
            ansi = this.map.get(meta);
            if (ansi == null) {
                throw new IllegalArgumentException(
                    String.format("unknown color '%s'", meta)
                );
            }
        } else {
            ansi = meta;
        }
        return ansi;
    }
}

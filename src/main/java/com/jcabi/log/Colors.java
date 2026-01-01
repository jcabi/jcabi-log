/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Store human readable color data.
 * @since 0.18
 */
public class Colors {

    /**
     * Colors with names.
     */
    private final transient ConcurrentMap<String, String> map;

    /**
     * Public ctor.
     */
    public Colors() {
        this.map = Colors.colorMap();
    }

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

    /**
     * Color map.
     * @return Map of colors
     */
    private static ConcurrentMap<String, String> colorMap() {
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<>(0);
        map.put("black", "30");
        map.put("blue", "34");
        map.put("cyan", "36");
        map.put("green", "32");
        map.put("magenta", "35");
        map.put("red", "31");
        map.put("yellow", "33");
        map.put("white", "37");
        return map;
    }
}

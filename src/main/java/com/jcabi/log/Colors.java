/*
 * Copyright (c) 2012-2022, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
            new ConcurrentHashMap<>();
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

/*
 * Copyright (c) 2012-2025 Yegor Bugayenko
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

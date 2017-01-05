/**
 * Copyright (c) 2012-2017, jcabi.com
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
import java.util.Map;

/**
 * Converts items inside a string like K1:V1,K2:V2 - where K is for key and V
 * is for value - to a {@code Map} of string key and string value.
 * @author Jose V. Dal Pra Junior (jrdalpra@gmail.com)
 * @version $Id$
 * @since 0.18
 */
class ParseableInformation {

    /**
     * Information content to be parsed.
     */
    private final transient String content;

    /**
     * Construtor.
     * @param cont Content to be parsed
     */
    public ParseableInformation(final String cont) {
        super();
        this.content = cont;
    }

    /**
     * Parse the information.
     * @return A {@link Map} with a key,value pair os strings
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    public final Map<String, String> information() {
        final Map<String, String> parsed = new HashMap<String, String>();
        try {
            for (final String item : this.items()) {
                final String[] values = item.split(":");
                parsed.put(values[0], values[1]);
            }
        } catch (final ArrayIndexOutOfBoundsException ex) {
            throw new IllegalStateException(
                String.format(new StringBuilder()
                    .append("Information is not using the pattern ")
                    .append("KEY1:VALUE,KEY2:VALUE %s")
                    .toString(),
                    this.content
                ), ex
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

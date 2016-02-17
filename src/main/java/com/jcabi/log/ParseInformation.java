/**
 * Copyright (c) 2012-2015, jcabi.com
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

/**
 * Converts items inside a string like K1:V1,K2:V2. - where K is for key and v
 * is for value - to a {@link Map} of string key and string value.
 *
 * @author Jose V. Dal Pra Junior (jrdalpra@gmail.com)
 * @version $Id$
 *
 */
class ParseInformation {

    /**
     * To split strings with javascript like map syntax.
     */
    public static final String SPLIT_ITEMS = ",";

    /**
     * To split key:value pairs.
     */
    public static final String SPLIT_VALUES = ":";

    /**
     * Information to be parsed.
     */
    private final transient String information;

    /**
     * Construtor.
     *
     * @param info To be parsed.
     */
    public ParseInformation(final String info) {
        super();
        this.information = info;
    }

    /**
     * Parse the information.
     *
     * @return A {@link ConcurrentHashMap} with a key,value pair os strings.
     */
    public final ConcurrentHashMap<String, String> parse() {
        final ConcurrentHashMap<String, String> parsed =
            new ConcurrentHashMap<String, String>();
        for (final String item : this.items()) {
            final String[] values = item.split(ParseInformation.SPLIT_VALUES);
            parsed.put(values[0], values[1]);
        }
        return parsed;
    }

    /**
     * Spring the information using {@link ParseInformation#SPLIT_ITEMS}
     * pattern.
     *
     * @return An array of items.
     */
    private String[] items() {
        return this.information.split(ParseInformation.SPLIT_ITEMS);
    }
}

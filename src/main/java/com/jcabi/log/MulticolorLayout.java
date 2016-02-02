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

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Multi-color layout for LOG4J.
 *
 * <p>Use it in your LOG4J configuration:
 *
 * <pre> log4j.rootLogger=INFO, CONSOLE
 * log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
 * log4j.appender.CONSOLE.layout=com.jcabi.log.MulticolorLayout
 * log4j.appender.CONSOLE.layout.ConversionPattern=[%color{%-5p}] %c: %m%n</pre>
 *
 * <p>The part of the message wrapped with {@code %color{...}}
 * will change its color according to the logging level of the event. Without
 * this highlighting the behavior of the layout is identical to
 * {@link EnhancedPatternLayout}. You can use {@code %color-red{...}} if you
 * want to use specifically red color for the wrapped piece of text. Supported
 * colors are: {@code red}, {@code blue}, {@code yellow}, {@code cyan},
 * {@code black}, and {@code white}.
 *
 * <p>Besides that you can specify any ANSI color you like with
 * {@code %color-<attr>;<bg>;<fg>{...}}, where
 * {@code <attr>} is a binary mask of attributes,
 * {@code <bg>} is a background color, and
 * {@code <fg>} is a foreground color. Read more about
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">ANSI escape code</a>.
 *
 * <p>This class or its parents are <b>not</b> serializable.
 *
 * <p>Maven dependency for this class is
 * (see <a href="http://www.jcabi.com/jcabi-log/multicolor.html">How
 * to use with Maven</a> instructions):
 *
 * <pre>&lt;dependency&gt;
 *  &lt;groupId&gt;com.jcabi&lt;/groupId&gt;
 *  &lt;artifactId&gt;jcabi-log&lt;/artifactId&gt;
 * &lt;/dependency&gt;</pre>
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1.10
 * @see <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">ANSI escape code</a>
 * @see <a href="http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html">PatternLayout from LOG4J</a>
 * @see <a href="http://www.jcabi.com/jcabi-log/multicolor.html">How to use with Maven</a>
 * @todo #59:30min This class is still handling multiple responsibilities like
 *  formatting, parsing and coloring and should be refactored.
 */
@ToString
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("PMD.NonStaticInitializer")
public final class MulticolorLayout extends EnhancedPatternLayout {
    /**
     * Control sequence indicator.
     */
    private static final String CSI = "\u001b[";

    /**
     * To split strings with javascript like map syntax.
     */
    private static final String SPLIT_ITEMS = ",";

    /**
     * To split key:value pairs.
     */
    private static final String SPLIT_VALUES = ":";

    /**
     * Regular expression for all matches.
     */
    private static final Pattern METAS = Pattern.compile(
        "%color(?:-([a-z]+|[0-9]{1,3};[0-9]{1,3};[0-9]{1,3}))?\\{(.*?)\\}"
    );

    /**
     * A format string for a color placeholder.
     */
    private static final String COLOR_PLACEHOLDER = "%s?m";

    /**
     * Colors of levels.
     */
    private final transient ConcurrentMap<String, String> levels =
        MulticolorLayout.levelMap();

    /**
     * Store original conversation pattern to be able
     * to recalculate it, if new colors are provided.
     */
    private transient String base;

    /**
     * Color human readable data.
     */
    private final transient Colors colors = new Colors();

    /**
     * Used to decide whether the logged text should be colored or not.
     */
    private final transient TextDecolorant decolorant;

    /**
     * Simple constructor.
     */
    public MulticolorLayout() {
        this(new RtTextDecolorant());
    }

    /**
     * Constructor that allos to specify the decolorant type.
     * @param dclrnt The given decolorant.
     */
    public MulticolorLayout(final TextDecolorant dclrnt) {
        super();
        this.decolorant = dclrnt;
    }

    @Override
    public void setConversionPattern(final String pattern) {
        this.base = pattern;
        final Matcher matcher = MulticolorLayout.METAS.matcher(pattern);
        final StringBuffer buf = new StringBuffer(0);
        while (matcher.find()) {
            matcher.appendReplacement(buf, "");
            buf.append(MulticolorLayout.CSI)
                .append(this.colors.ansi(matcher.group(1)))
                .append('m')
                .append(matcher.group(2))
                .append(MulticolorLayout.CSI)
                .append('m');
        }
        matcher.appendTail(buf);
        super.setConversionPattern(buf.toString());
    }

    /**
     * Allow to overwrite or specify new ANSI color names
     * in a javascript map like format.
     *
     * @param cols JavaScript like map of color names
     * @since 0.9
     */
    public void setColors(final String cols) {
        for (final String item : cols.split(MulticolorLayout.SPLIT_ITEMS)) {
            final String[] values = item.split(MulticolorLayout.SPLIT_VALUES);
            this.colors.addColor(values[0], values[1]);
        }
        /**
         * If setConversionPattern was called before me must call again
         * to be sure to replace all custom color constants with
         * new values.
         */
        if (this.base != null) {
            this.setConversionPattern(this.base);
        }
    }

    /**
     * Allow to overwrite the ANSI color values for the log levels
     * in a javascript map like format.
     *
     * @param lev JavaScript like map of levels
     * @since 0.9
     */
    public void setLevels(final String lev) {
        for (final String item : lev.split(MulticolorLayout.SPLIT_ITEMS)) {
            final String[] values = item.split(MulticolorLayout.SPLIT_VALUES);
            final String level = values[0].toUpperCase(Locale.ENGLISH);
            if (Level.toLevel(level, null) == null) {
                throw new IllegalArgumentException(
                    String.format(Locale.ENGLISH, "Unknown level '%s'", level)
                );
            }
            this.levels.put(level, values[1]);
        }
    }

    @Override
    public String format(final LoggingEvent event) {
        final String answer;
        if (this.decolorant.isColoringEnabled()) {
            answer = this.colorfulFormatting(event);
        } else {
            answer = this.dullFormatting(event);
        }
        return answer;
    }

    /**
     * Formats a log event without using ANSI color codes.
     * @param event Log event
     * @return Text of a log event, not colored with ANSI color codes even
     *  if there is markup that tells to color it.
     */
    private String dullFormatting(final LoggingEvent event) {
        return super.format(event)
            .replace(
                String.format(
                    MulticolorLayout.COLOR_PLACEHOLDER,
                    MulticolorLayout.CSI
                ),
                ""
            )
            .replace(String.format("%sm", MulticolorLayout.CSI), "");
    }

    /**
     * Formats a log event using ANSI color codes.
     * @param event Log event
     * @return Text of a log event, probably colored with ANSI color codes.
     */
    private String colorfulFormatting(final LoggingEvent event) {
        return super.format(event).replace(
            String.format(COLOR_PLACEHOLDER, MulticolorLayout.CSI),
            String.format(
                "%s%sm",
                MulticolorLayout.CSI,
                this.levels.get(event.getLevel().toString())
            )
        );
    }

    /**
     * Level map.
     * @return Map of levels
     */
    private static ConcurrentMap<String, String> levelMap() {
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<String, String>();
        map.put(Level.TRACE.toString(), "2;33");
        map.put(Level.DEBUG.toString(), "2;37");
        map.put(Level.INFO.toString(), "0;37");
        map.put(Level.WARN.toString(), "0;33");
        map.put(Level.ERROR.toString(), "0;31");
        map.put(Level.FATAL.toString(), "0;35");
        return map;
    }

}

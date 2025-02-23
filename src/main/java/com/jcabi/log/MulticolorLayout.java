/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
 * @since 0.1.10
 * @see <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">ANSI escape code</a>
 * @see <a href="http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html">PatternLayout from LOG4J</a>
 * @see <a href="http://www.jcabi.com/jcabi-log/multicolor.html">How to use with Maven</a>
 */
@SuppressWarnings("PMD.NonStaticInitializer")
public final class MulticolorLayout extends EnhancedPatternLayout {

    /**
     * Name of the property that is used to disable log coloring.
     */
    private static final String COLORING_PROPERY = "com.jcabi.log.coloring";

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

    @Override
    public void setConversionPattern(final String pattern) {
        this.base = pattern;
        super.setConversionPattern(
            new ConversionPattern(this.base, this.colors).generate()
        );
    }

    /**
     * Allow to overwrite or specify new ANSI color names
     * in a javascript map like format.
     *
     * @param cols JavaScript like map of color names
     * @since 0.9
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    public void setColors(final String cols) {
        final Map<String, String> parsed = new ParseableInformation(
            cols
        ).information();
        for (final Map.Entry<String, String> entry : parsed.entrySet()) {
            this.colors.addColor(entry.getKey(), entry.getValue());
        }
        if (this.base != null) {
            this.setConversionPattern(this.base);
        }
    }

    /**
     * Allow to overwrite the ANSI color values for the log levels
     * in a javascript map like format.
     * @param lev JavaScript like map of levels
     * @since 0.9
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    public void setLevels(final String lev) {
        final Map<String, String> parsed = new ParseableLevelInformation(
            lev
        ).information();
        for (final Map.Entry<String, String> entry : parsed.entrySet()) {
            this.levels.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String format(final LoggingEvent event) {
        final Formatted formatted;
        if (MulticolorLayout.isColoringEnabled()) {
            formatted = this.colorfulFormatting(event);
        } else {
            formatted = this.dullFormatting(event);
        }
        return formatted.format();
    }

    /**
     * Generate a dull {@code Formatted}.
     * @param event Event to be formatted
     * @return A {@link Formatted} to format the event
     * @checkstyle NonStaticMethodCheck (10 lines)
     */
    private Formatted dullFormatting(final LoggingEvent event) {
        return new DullyFormatted(super.format(event));
    }

    /**
     * Generate a colorful {@code Formatted}.
     * @param event Event to be formatted
     * @return Text of a log event, probably colored with ANSI color codes
     */
    private Formatted colorfulFormatting(final LoggingEvent event) {
        return new ColorfullyFormatted(
            super.format(event),
            this.levels.get(event.getLevel().toString())
        );
    }

    /**
     * Level map.
     * @return Map of levels
     */
    private static ConcurrentMap<String, String> levelMap() {
        final ConcurrentMap<String, String> map = new ConcurrentHashMap<>(0);
        map.put(Level.TRACE.toString(), "2;33");
        map.put(Level.DEBUG.toString(), "2;37");
        map.put(Level.INFO.toString(), "0;37");
        map.put(Level.WARN.toString(), "0;33");
        map.put(Level.ERROR.toString(), "0;31");
        map.put(Level.FATAL.toString(), "0;35");
        return map;
    }

    /**
     * Should the logged text be colored or not.
     * @return True if the coloring is enabled, or false otherwise.
     */
    private static boolean isColoringEnabled() {
        return !"false".equals(
            System.getProperty(MulticolorLayout.COLORING_PROPERY)
        );
    }

}

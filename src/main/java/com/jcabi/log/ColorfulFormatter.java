package com.jcabi.log;

/**
 * Formats a log event using ANSI color codes.
 * 
 * @author Jose V. Dal Pra Junior (jrdalpra@gmail.com)
 * @version $Id$
 *
 */
class ColorfulFormatter implements Formatter {

    /**
     * The basic information to be colorful formatted.
     */
    private final String basic;
    
    /**
     * Color used as the replacement.
     */
    private final String color;

    public ColorfulFormatter(String basic, String color) {
        super();
        this.basic = basic;
        this.color = color;
    }

    /**
     * Formats a log event using ANSI color codes.
     * 
     * @return Text of a log event, probably colored with ANSI color codes.
     */
    @Override
    public String format() {
        return basic.replace(
            String.format(Constants.COLOR_PLACEHOLDER, Constants.CSI),
            String.format("%s%sm", Constants.CSI, color));
    }
}

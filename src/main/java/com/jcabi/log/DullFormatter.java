package com.jcabi.log;

class DullFormatter implements Formatter {

    /**
     * String to be formatted.
     */
    private final String basic;

    /**
     * Contructor.
     * @param basic String to be formatted.
     */
    public DullFormatter(String basic) {
        super();
        this.basic = basic;
    }

    /**
     * Formats a log event without using ANSI color codes.
     * @return Text of a log event, not colored with ANSI color codes even
     *  if there is markup that tells to color it.
     */
    @Override
    public String format() {
        return basic.replace(
            String.format(
                Constants.COLOR_PLACEHOLDER,
                Constants.CSI
            ),
            ""
        ).replace(String.format("%sm", Constants.CSI), "");
    }

}

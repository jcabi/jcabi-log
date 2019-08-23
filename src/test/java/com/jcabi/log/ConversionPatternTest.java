package com.jcabi.log;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ConversionPatternTest {
    private static final String CSI = "\u001b\\[";
    private static final Colors COLORS = new Colors();

    @Test
    public void testGenerateNoReplacement() {
        MatcherAssert.assertThat(convert(""), Matchers.equalTo(""));
        MatcherAssert.assertThat(convert("foo"), Matchers.equalTo("foo"));
        MatcherAssert.assertThat(convert("%color"), Matchers.equalTo("%color"));
        MatcherAssert.assertThat(convert("%color-"), Matchers.equalTo("%color-"));
        MatcherAssert.assertThat(convert("%color{%c{1}foo"), Matchers.equalTo("%color{%c{1}foo"));
    }

    @Test
    public void testGenerateEmpty() {
        MatcherAssert.assertThat(convert("%color{}"), Matchers.equalTo(CSI + "?m" + CSI + "m"));
    }

    @Test
    public void testGenerateSimple() {
        MatcherAssert.assertThat(convert("%color{Hello World}"), Matchers.equalTo(CSI + "?mHello World" + CSI + "m"));
        MatcherAssert.assertThat(convert("%color{Hello World}foo"), Matchers.equalTo(CSI + "?mHello World" + CSI + "mfoo"));

        MatcherAssert.assertThat(convert("%color{Hello}%color{World}"), Matchers.equalTo(CSI + "?mHello" + CSI + "m" + CSI + "?mWorld" + CSI + "m"));
    }

    @Test
    public void testGenerateCurlyBraces() {
        MatcherAssert.assertThat(convert("%color{%c{1}}"), Matchers.equalTo(CSI + "?m%c{1}" + CSI + "m"));
        MatcherAssert.assertThat(convert("%color{%c{1}}foo"), Matchers.equalTo(CSI + "?m%c{1}" + CSI + "mfoo"));
        MatcherAssert.assertThat(convert("%color{%c1}}foo"), Matchers.equalTo(CSI + "?m%c1" + CSI + "m}foo"));
        MatcherAssert.assertThat(convert("%color{%c{{{1}{2}}}}foo"), Matchers.equalTo(CSI + "?m%c{{{1}{2}}}" + CSI + "mfoo"));
    }

    private static String convert(final String pat) {
        return new ConversionPattern(pat, COLORS).generate();
    }
}

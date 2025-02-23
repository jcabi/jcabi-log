/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.util.Formattable;
import java.util.Formatter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.w3c.dom.Document;

/**
 * Test case for {@link DomDecor}.
 *
 * @since 0.1
 */
final class DomDecorTest {

    @Test
    void convertsDocumentToText() throws Exception {
        final Document doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().newDocument();
        doc.appendChild(doc.createElement("root"));
        final Formattable decor = new DomDecor(doc);
        final Appendable dest = Mockito.mock(Appendable.class);
        final Formatter fmt = new Formatter(dest);
        decor.formatTo(fmt, 0, 0, 0);
        Mockito.verify(dest).append(
            MockitoHamcrest.argThat(Matchers.containsString("<root/>"))
        );
    }

    @Test
    void convertsNullToText() throws Exception {
        final Formattable decor = new DomDecor(null);
        final Appendable dest = Mockito.mock(Appendable.class);
        final Formatter fmt = new Formatter(dest);
        decor.formatTo(fmt, 0, 0, 0);
        Mockito.verify(dest).append("NULL");
    }

}

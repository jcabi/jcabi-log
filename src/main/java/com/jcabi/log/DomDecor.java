/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.log;

import java.io.StringWriter;
import java.util.Formattable;
import java.util.Formatter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

/**
 * Decorates XML Document.
 *
 * @since 0.1
 */
final class DomDecor implements Formattable {

    /**
     * DOM transformer factory, DOM.
     */
    private static final TransformerFactory FACTORY =
        TransformerFactory.newInstance();

    /**
     * The document.
     */
    private final transient Node node;

    /**
     * Public ctor.
     * @param doc The document
     * @throws DecorException If some problem with it
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    DomDecor(final Object doc) throws DecorException {
        if (doc != null && !(doc instanceof Node)) {
            throw new DecorException(
                String.format(
                    "Instance of org.w3c.dom.Node required, while %s provided",
                    doc.getClass().getName()
                )
            );
        }
        this.node = (Node) doc;
    }

    // @checkstyle ParameterNumber (4 lines)
    @Override
    public void formatTo(final Formatter formatter, final int flags,
        final int width, final int precision) {
        final StringWriter writer = new StringWriter();
        if (this.node == null) {
            writer.write("NULL");
        } else {
            try {
                final Transformer trans = DomDecor.FACTORY.newTransformer();
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                trans.setOutputProperty(OutputKeys.STANDALONE, "no");
                trans.transform(
                    new DOMSource(this.node),
                    new StreamResult(writer)
                );
            } catch (final TransformerException ex) {
                throw new IllegalStateException(ex);
            }
        }
        formatter.format("%s", writer);
    }

}

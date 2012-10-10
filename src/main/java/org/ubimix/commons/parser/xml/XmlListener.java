package org.ubimix.commons.parser.xml;

import java.util.Map;

import org.ubimix.commons.parser.ParserListener;

/**
 * @author kotelnikov
 */
public class XmlListener extends ParserListener
    implements
    IXmlListener {

    @Override
    public void beginElement(
        String tagName,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
    }

    @Override
    public void endElement(
        String tagName,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
    }

    @Override
    public void onCDATA(String content) {
    }

    @Override
    public void onComment(String commentContent) {
    }

    @Override
    public void onEntity(Entity entity) {
    }

    @Override
    public void onText(String string) {
    }

}
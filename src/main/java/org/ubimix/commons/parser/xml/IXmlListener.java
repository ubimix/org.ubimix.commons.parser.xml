package org.ubimix.commons.parser.xml;

import java.util.Map;

import org.ubimix.commons.parser.IParserListener;

/**
 * @author kotelnikov
 */
public interface IXmlListener extends IParserListener {

    void beginElement(
        String tagName,
        Map<String, String> attributes,
        Map<String, String> namespaces);

    void endElement(
        String tagName,
        Map<String, String> attributes,
        Map<String, String> namespaces);

    void onCDATA(String content);

    void onComment(String commentContent);

    void onEntity(Entity entity);

    void onText(String string);
}
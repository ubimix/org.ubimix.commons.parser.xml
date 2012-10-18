/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.xml.CommentTokenizer.CommentToken;

/**
 * @author kotelnikov
 */
public class XmlParser extends AbstractXmlParser {

    /**
     * This class is used to handle tag attributes
     */
    protected static class TagInfo {

        private static final String NS = "xmlns";

        private static final String NS_PREFIX = "xmlns:";

        private Map<String, String> fAttributes = new LinkedHashMap<String, String>();

        private Map<String, String> fNamespaces = new LinkedHashMap<String, String>();

        private TagInfo fParent;

        private String fTagName;

        public TagInfo(
            TagInfo parent,
            String tagName,
            Map<String, String> attributes) {
            fParent = parent;
            fTagName = tagName;
            if (attributes != null) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String prefix = null;
                    if (key.startsWith(NS_PREFIX)) {
                        prefix = key.substring(NS_PREFIX.length());
                    } else if (key.equals(NS)) {
                        prefix = "";
                    }
                    if (prefix != null) {
                        fNamespaces.put(prefix, value);
                    } else {
                        fAttributes.put(key, value);
                    }
                }
            }
        }

        public void beginElement(IXmlListener listener) {
            listener.beginElement(fTagName, fAttributes, fNamespaces);
        }

        public void checkTagName(String tagName) {
            if (!fTagName.equals(tagName)) {
                throw new IllegalStateException("A closing tag for '"
                    + fTagName
                    + "' is expected but '"
                    + tagName
                    + "' was found.");
            }
        }

        public TagInfo endElement(IXmlListener listener) {
            listener.endElement(fTagName, fAttributes, fNamespaces);
            return fParent;
        }
    }

    protected StringBuilder fBuf = new StringBuilder();

    protected TagInfo fPeek;

    /**
     * 
     */
    public XmlParser() {
        this(XMLTokenizer.getFullXMLTokenizer());
    }

    public XmlParser(ITokenizer tokenizer) {
        super(tokenizer);
    }

    private void appendText(String content) {
        fBuf.append(content);
    }

    protected boolean check(StreamToken token, Class<?> type) {
        return type.isInstance(token);
    }

    @Override
    protected void finishParse() {
        flushText();
        while (fPeek != null) {
            fPeek = fPeek.endElement(fListener);
        }
    }

    protected void flushText() {
        if (fBuf.length() > 0) {
            if (fPeek != null) {
                fListener.onText(fBuf.toString());
            }
            fBuf.delete(0, fBuf.length());
        }
    }

    protected void pop(String tagName) {
        if (fPeek != null) {
            fPeek.checkTagName(tagName);
            fPeek = fPeek.endElement(fListener);
        }
    }

    protected void push(String tagName, Map<String, String> attributes) {
        fPeek = new TagInfo(fPeek, tagName, attributes);
        fPeek.beginElement(fListener);
    }

    protected void reportCDATA(CDATAToken token) {
        flushText();
        if (fPeek != null) {
            fListener.onCDATA(token.getCDATAValue());
        }
    }

    protected void reportComment(CommentToken token) {
        fListener.onComment(token.getCommentContent());
    }

    protected void reportEntity(EntityToken token) {
        Entity entity = token.getEntityKey();
        fListener.onEntity(entity);
    }

    protected void reportEOL(StreamToken token) {
        appendText(token.getText());
    }

    protected void reportProcessingInstructions(StreamToken token) {
        // TODO Auto-generated method stub
    }

    protected void reportProlog(StreamToken token) {
        // TODO Auto-generated method stub
    }

    protected void reportSpaces(StreamToken token) {
        appendText(token.getText());
    }

    protected void reportSpecialSymbols(StreamToken token) {
        appendText(token.getText());
    }

    protected void reportTag(TagToken token) {
        flushText();
        String tagName = token.getName();
        if (token.isOpen()) {
            Map<String, String> attributes = token.getAttributesAsMap();
            if (attributes == null) {
                attributes = Collections.emptyMap();
            }
            push(tagName, attributes);
        }
        if (token.isClose()) {
            pop(tagName);
        }
    }

    protected void reportWord(StreamToken token) {
        appendText(token.getText());
    }

}

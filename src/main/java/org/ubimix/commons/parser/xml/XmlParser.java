/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ubimix.commons.parser.AbstractParser;
import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.ITokenizer.StreamToken;
import org.ubimix.commons.parser.text.TextDict;
import org.ubimix.commons.parser.xml.CommentTokenizer.CommentToken;

/**
 * @author kotelnikov
 */
public class XmlParser extends AbstractParser<XmlParser.IXmlParserListener> {

    public interface IXmlParserListener extends AbstractParser.IParserListener {

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

    /**
     * This class is used to handle tag attributes
     */
    private static class TagInfo {

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

        public void beginElement(IXmlParserListener listener) {
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

        public TagInfo endElement(IXmlParserListener listener) {
            listener.endElement(fTagName, fAttributes, fNamespaces);
            return fParent;
        }
    }

    /**
     * @author kotelnikov
     */
    public static class XmlParserListener extends ParserListener
        implements
        IXmlParserListener {

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

    private StringBuilder fBuf = new StringBuilder();

    private TagInfo fPeek;

    /**
     * 
     */
    public XmlParser() {
        this(XMLTokenizer.getFullXMLTokenizer());
    }

    protected XmlParser(ITokenizer tokenizer) {
        super(tokenizer);
    }

    private void appendText(String content) {
        fBuf.append(content);
    }

    private boolean check(StreamToken token, Class<?> type) {
        return type.isInstance(token);
    }

    @Override
    protected void doParse() {
        StreamToken token = getToken(true);
        while (token != null) {
            // XML:
            // public final static String CDATA = "CDATA";
            // public final static String COMMENT = "comment";
            // public final static String ENTITY = "entity";
            // public final static String PROCESSING = "processing";
            // public final static String PROLOG = "prolog";
            // public final static String TAG = "tag";
            // Text:
            // public final static String EOL = "eol";
            // public final static String SPACES = "spaces";
            // public final static String SPECIAL = "special";
            // public final static String WORD = "word";
            if (check(token, TextDict.SpacesToken.class)) {
                reportSpaces(token);
            } else if (check(token, TextDict.WordToken.class)) {
                reportWord(token);
            } else if (check(token, TextDict.NewLineToken.class)) {
                reportEOL(token);
            } else if (check(token, TagToken.class)) {
                reportTag((TagToken) token);
            } else if (check(token, TextDict.SpecialSymbolsToken.class)) {
                reportSpecialSymbols(token);
            } else if (check(token, CDATAToken.class)) {
                reportCDATA((CDATAToken) token);
            } else if (check(token, CommentToken.class)) {
                reportComment((CommentToken) token);
            } else if (check(token, EntityToken.class)) {
                reportEntity((EntityToken) token);
            } else if (check(token, ProcessingInstructionToken.class)) {
                reportProcessingInstructions(token);
            } else if (check(token, PrologToken.class)) {
                reportProlog(token);
            }
            token = getToken(true);
        }
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

    private void reportCDATA(CDATAToken token) {
        flushText();
        if (fPeek != null) {
            fListener.onCDATA(token.getCDATAValue());
        }
    }

    private void reportComment(CommentToken token) {
        fListener.onComment(token.getCommentContent());
    }

    private void reportEntity(EntityToken token) {
        Entity entity = token.getEntityKey();
        fListener.onEntity(entity);
    }

    private void reportEOL(StreamToken token) {
        appendText(token.getText());
    }

    private void reportProcessingInstructions(StreamToken token) {
        // TODO Auto-generated method stub
    }

    private void reportProlog(StreamToken token) {
        // TODO Auto-generated method stub
    }

    private void reportSpaces(StreamToken token) {
        appendText(token.getText());
    }

    private void reportSpecialSymbols(StreamToken token) {
        appendText(token.getText());
    }

    private void reportTag(TagToken token) {
        flushText();
        String tagName = token.getName();
        if (token.isOpen()) {
            Map<String, String> attributes = token.getAttributesAsMap();
            if (attributes == null) {
                attributes = Collections.emptyMap();
            }
            fPeek = new TagInfo(fPeek, tagName, attributes);
            fPeek.beginElement(fListener);
        }
        if (token.isClose()) {
            if (fPeek != null) {
                fPeek.checkTagName(tagName);
                fPeek = fPeek.endElement(fListener);
            }
        }
    }

    private void reportWord(StreamToken token) {
        appendText(token.getText());
    }
}

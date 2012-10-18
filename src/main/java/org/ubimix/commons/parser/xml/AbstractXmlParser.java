/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.AbstractParser;
import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.text.TextDict;
import org.ubimix.commons.parser.xml.CommentTokenizer.CommentToken;

/**
 * @author kotelnikov
 */
public abstract class AbstractXmlParser extends AbstractParser<IXmlListener>
    implements
    IXmlParser {

    public AbstractXmlParser() {
        this(XMLTokenizer.getFullXMLTokenizer());
    }

    public AbstractXmlParser(ITokenizer tokenizer) {
        super(tokenizer);
    }

    protected boolean check(StreamToken token, Class<?> type) {
        return type.isInstance(token);
    }

    protected void dispatchToken(StreamToken token) {
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
    }

    @Override
    protected void doParse() {
        StreamToken token = getToken(true);
        while (token != null) {
            dispatchToken(token);
            token = getToken(true);
        }
        finishParse();
    }

    protected abstract void finishParse();

    protected abstract void reportCDATA(CDATAToken token);

    protected abstract void reportComment(CommentToken token);

    protected abstract void reportEntity(EntityToken token);

    protected abstract void reportEOL(StreamToken token);

    protected abstract void reportProcessingInstructions(StreamToken token);

    protected abstract void reportProlog(StreamToken token);

    protected abstract void reportSpaces(StreamToken token);

    protected abstract void reportSpecialSymbols(StreamToken token);

    protected abstract void reportTag(TagToken token);

    protected abstract void reportWord(StreamToken token);

}

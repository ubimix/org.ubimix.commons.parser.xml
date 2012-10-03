/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.base.DelimitedTextTokenizer;

public class CommentTokenizer extends DelimitedTextTokenizer {

    public static class CommentToken extends StreamToken {
        public String getCommentContent() {
            String str = getText();
            str = str.substring(BEGIN.length());
            str = str.substring(0, str.length() - END.length());
            return str;
        }
    }

    private static final String BEGIN = "<!--";

    private static final String END = "-->";

    public static final CommentTokenizer INSTANCE = new CommentTokenizer();

    public CommentTokenizer() {
        super(BEGIN, END, false);
    }

    @Override
    protected StreamToken newToken() {
        return new CommentToken();
    }

}
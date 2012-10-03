/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.util.List;

import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.base.DelimitedTextTokenizer;

/**
 * @author kotelnikov
 */
public class PrologTokenizer extends DelimitedTextTokenizer {

    public static final PrologTokenizer INSTANCE = new PrologTokenizer();

    /**
     * 
     */
    public PrologTokenizer() {
        super("<!", ">", true);
    }

    @Override
    protected void addSubtokens(StreamToken parent, List<StreamToken> children) {
        PrologToken p = (PrologToken) parent;
        p.setChildren(children);
    }

    @Override
    protected StreamToken newToken() {
        return new PrologToken();
    }

}

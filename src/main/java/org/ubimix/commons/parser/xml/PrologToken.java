/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.ITokenizer.StreamToken;

/**
 * @author kotelnikov
 */
public class PrologToken extends StreamToken {

    private List<PrologToken> fChildren = new ArrayList<PrologToken>();

    public List<PrologToken> getChildren() {
        return fChildren;
    }

    public void setChildren(List<StreamToken> children) {
        fChildren.clear();
        for (StreamToken token : children) {
            if (token instanceof PrologToken) {
                fChildren.add((PrologToken) token);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + fChildren;
    }
}
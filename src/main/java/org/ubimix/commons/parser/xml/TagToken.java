/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.CharStream.Pointer;
import org.ubimix.commons.parser.StreamToken;

/**
 * @author kotelnikov
 */
public class TagToken extends StreamToken {

    private List<AttrToken> fAttributes;

    private boolean fClose;

    private String fName;

    private Pointer fNameBegin;

    private Pointer fNameEnd;

    private boolean fOpen;

    public TagToken() {
    }

    /**
     * @return the firstAttribute
     */
    public List<AttrToken> getAttributes() {
        return fAttributes != null ? fAttributes : Collections
            .<AttrToken> emptyList();
    }

    public Map<String, String> getAttributesAsMap() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (fAttributes != null) {
            for (AttrToken token : fAttributes) {
                String name = token.getName();
                String value = token.getValueOnly();
                result.put(name, value);
            }
        }
        return result;
    }

    /**
     * @return the name
     */
    public String getName() {
        return fName;
    }

    /**
     * @return the nameBegin
     */
    public Pointer getNameBegin() {
        return fNameBegin;
    }

    /**
     * @return the nameEnd
     */
    public Pointer getNameEnd() {
        return fNameEnd;
    }

    protected TagToken init(boolean open, boolean close) {
        fOpen = open;
        fClose = close;
        return this;
    }

    public boolean isClose() {
        return fClose;
    }

    public boolean isOpen() {
        return fOpen;
    }

    /**
     * @param firstAttribute the firstAttribute to set
     */
    public void setAttributes(List<AttrToken> attributes) {
        fAttributes = attributes;
    }

    public void setName(Pointer begin, Pointer end, String str) {
        fNameBegin = begin;
        fNameEnd = end;
        fName = str;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + fName + ")";
    }

}
/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.StreamToken;

/**
 * @author kotelnikov
 */
public class AttrToken extends StreamToken {

    private String fName;

    private ICharStream.IPointer fNameBegin;

    private ICharStream.IPointer fNameEnd;

    private String fValue;

    private ICharStream.IPointer fValueBegin;

    private ICharStream.IPointer fValueEnd;

    /**
     * @return the name
     */
    public String getName() {
        return fName;
    }

    /**
     * @return the nameBegin
     */
    public ICharStream.IPointer getNameBegin() {
        return fNameBegin;
    }

    /**
     * @return the nameEnd
     */
    public ICharStream.IPointer getNameEnd() {
        return fNameEnd;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return fValue;
    }

    /**
     * @return the valueBegin
     */
    public ICharStream.IPointer getValueBegin() {
        return fValueBegin;
    }

    /**
     * @return the valueEnd
     */
    public ICharStream.IPointer getValueEnd() {
        return fValueEnd;
    }

    public String getValueOnly() {
        String value = getValue();
        if (value.startsWith("'") || value.startsWith("\"")) {
            value = value.substring(1);
        }
        if (value.endsWith("'") || value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    void setName(ICharStream.IPointer begin, ICharStream.IPointer end, String str) {
        fNameBegin = begin;
        fNameEnd = end;
        fName = str;
    }

    void setValue(ICharStream.IPointer begin, ICharStream.IPointer end, String str) {
        fValueBegin = begin;
        fValueEnd = end;
        fValue = str;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + fName + "=" + fValue + ")";
    }

}